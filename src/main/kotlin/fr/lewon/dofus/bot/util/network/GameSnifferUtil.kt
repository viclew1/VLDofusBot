package fr.lewon.dofus.bot.util.network

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiver
import fr.lewon.dofus.bot.sniffer.Host
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.GlobalConfigManager
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.listenable.ListenableByCharacter
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock


object GameSnifferUtil : ListenableByCharacter<GameSnifferListener>() {

    private const val netstatCommand = "cmd.exe /c netstat -aop TCP -n | findstr :5555"
    private val netstatDofusRegex = Regex(
        "\\s+TCP" +
                "\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)" +
                "\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+):(5555)" +
                "\\s+ESTABLISHED" +
                "\\s+(\\d+)"
    )
    private val frameNameRegex = Regex("(.*?) - Dofus.*?")

    private val lock = ReentrantLock()
    private val connectionsByCharacterName = HashMap<String, ArrayList<DofusConnection>>()
    private val connectionsByGameInfo = HashMap<GameInfo, ArrayList<DofusConnection>>()
    private var messageReceiver = DofusMessageReceiver(GlobalConfigManager.readConfig().networkInterfaceName)

    fun updateNetworkInterface() {
        lock.executeSyncOperation {
            messageReceiver.kill()
            connectionsByCharacterName.clear()
            connectionsByGameInfo.clear()
            messageReceiver = DofusMessageReceiver(GlobalConfigManager.readConfig().networkInterfaceName)
            messageReceiver.start()
        }
    }

    fun updateNetwork() {
        startSnifferIfNeeded()
        updateCurrentConnections()
    }

    fun getGameInfoByConnection(connection: DofusConnection): GameInfo {
        return lock.executeSyncOperation {
            connectionsByGameInfo.entries.firstOrNull { it.value.contains(connection) }?.key
                ?: error("There is no game info associated to connection : ${connection.pid}")
        }
    }

    fun getFirstConnection(character: DofusCharacter): DofusConnection? {
        return getConnections(character).firstOrNull()
    }

    fun getConnections(character: DofusCharacter): List<DofusConnection> {
        return lock.executeSyncOperation {
            connectionsByCharacterName[character.name]?.toList() ?: emptyList()
        }
    }

    private fun startSnifferIfNeeded() {
        lock.executeSyncOperation {
            if (!messageReceiver.isSnifferRunning()) {
                messageReceiver.start()
                if (!WaitUtil.waitUntil({ messageReceiver.isSnifferRunning() })) {
                    error("Couldn't start sniffer")
                }
            }
        }
    }

    private fun updateCurrentConnections() {
        val findConnectionsProcessBuilder = ProcessBuilder(netstatCommand.split(" "))
        val process = findConnectionsProcessBuilder.start()
        if (process.waitFor(5L, TimeUnit.SECONDS)) {
            val runningConnections = BufferedReader(InputStreamReader(process.inputStream)).readLines()
                .mapNotNull { netstatDofusRegex.matchEntire(it) }
                .mapNotNull { parseDofusConnection(it) }
            val storedConnections = lock.executeSyncOperation {
                connectionsByCharacterName.values.flatten()
            }
            stopListeningToDeadConnections(storedConnections.filter { !runningConnections.contains(it) })
            listenToConnections(runningConnections.filter { !storedConnections.contains(it) })
        }
    }

    private fun parseDofusConnection(matchResult: MatchResult): DofusConnection? {
        val pid = matchResult.groupValues[5].trim().toLong()
        val characterName = getCharacterNameFromFrame(pid) ?: return null
        val client = Host(matchResult.groupValues[1].trim(), matchResult.groupValues[2].trim())
        val server = Host(matchResult.groupValues[3].trim(), matchResult.groupValues[4].trim())
        return DofusConnection(characterName, client, server, pid)
    }

    private fun getCharacterNameFromFrame(pid: Long): String? {
        return frameNameRegex.matchEntire(JNAUtil.getFrameName(pid))?.destructured?.component1()
    }

    private fun stopListeningToDeadConnections(deadConnections: List<DofusConnection>) {
        val removedCharacters = HashSet<DofusCharacter>()
        for (connection in deadConnections) {
            messageReceiver.stopListening(connection.client)
            removedCharacters.addAll(removeConnection(connection))
        }
        removedCharacters.forEach { character ->
            println("stop listening : ${character.name}")
            getListeners(character).forEach { listener ->
                listener.onListenStop(character)
            }
        }
    }

    private fun removeConnection(connection: DofusConnection): List<DofusCharacter> {
        return lock.executeSyncOperation {
            connectionsByCharacterName.entries.filter { it.value.contains(connection) }
                .forEach { it.value.remove(connection) }
            val toRemove = connectionsByCharacterName.entries.filter { it.value.isEmpty() }
            connectionsByCharacterName.entries.removeAll(toRemove.toSet())
            connectionsByGameInfo.entries.filter { it.value.contains(connection) }
                .forEach { it.value.remove(connection) }
            connectionsByGameInfo.entries.removeIf { it.value.isEmpty() }
            toRemove.mapNotNull { CharacterManager.getCharacter(it.key) }
        }
    }

    private fun listenToConnections(newConnections: List<DofusConnection>) {
        for (connection in newConnections) {
            val character = CharacterManager.getCharacter(connection.characterName)
                ?: CharacterManager.addCharacter(connection.characterName, 1, emptyList())
            val gameInfo = connectionsByGameInfo.keys.firstOrNull { it.character == character }
                ?: GameInfo(character).also { it.connection = connection }
            listenToConnection(gameInfo, character, connection)
        }
    }

    private fun listenToConnection(gameInfo: GameInfo, character: DofusCharacter, connection: DofusConnection) {
        gameInfo.shouldInitBoard = true
        messageReceiver.startListening(connection, gameInfo.eventStore, character.snifferLogger)
        lock.executeSyncOperation {
            connectionsByCharacterName.computeIfAbsent(character.name) { ArrayList() }.add(connection)
            connectionsByGameInfo.computeIfAbsent(gameInfo) { ArrayList() }.add(connection)
        }
        println("start listening (${GlobalConfigManager.readConfig().networkInterfaceName}) : ${character.name} ($connection)")
        getListeners(character).forEach { listener ->
            listener.onListenStart(character)
        }
    }

}