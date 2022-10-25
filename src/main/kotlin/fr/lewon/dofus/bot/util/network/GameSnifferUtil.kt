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

    private const val NETSTAT_COMMAND = "cmd.exe /c netstat -aop TCP -n | findstr :5555"
    private val NETSTAT_DOFUS_REGEX = Regex(
        "\\s+TCP" +
                "\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)" +
                "\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+):(5555)" +
                "\\s+ESTABLISHED" +
                "\\s+(\\d+)"
    )
    private val FRAME_NAME_REGEX = Regex("(.*?) - Dofus.*?")

    private val lock = ReentrantLock()

    private val CONNECTIONS_BY_CHARACTER_NAME = HashMap<String, ArrayList<DofusConnection>>()
    private val CONNECTIONS_BY_GAME_INFO = HashMap<GameInfo, ArrayList<DofusConnection>>()
    private var MESSAGE_RECEIVER = DofusMessageReceiver(GlobalConfigManager.readConfig().networkInterfaceName)

    fun updateNetworkInterface() {
        lock.executeSyncOperation {
            MESSAGE_RECEIVER.interrupt()
            MESSAGE_RECEIVER.join()

            CONNECTIONS_BY_CHARACTER_NAME.clear()
            CONNECTIONS_BY_GAME_INFO.clear()

            MESSAGE_RECEIVER = DofusMessageReceiver(GlobalConfigManager.readConfig().networkInterfaceName)
            MESSAGE_RECEIVER.start()
        }
    }

    fun updateNetwork() {
        startSnifferIfNeeded()
        updateCurrentConnections()
    }

    fun getGameInfoByConnection(connection: DofusConnection): GameInfo {
        return lock.executeSyncOperation {
            CONNECTIONS_BY_GAME_INFO.entries.firstOrNull { it.value.contains(connection) }?.key
                ?: error("There is no game info associated to connection : ${connection.pid}")
        }
    }

    fun getFirstConnection(character: DofusCharacter): DofusConnection? {
        return getConnections(character).firstOrNull()
    }

    fun getConnections(character: DofusCharacter): List<DofusConnection> {
        return lock.executeSyncOperation {
            CONNECTIONS_BY_CHARACTER_NAME[character.name]?.toList() ?: emptyList()
        }
    }

    private fun startSnifferIfNeeded() {
        lock.executeSyncOperation {
            if (!MESSAGE_RECEIVER.isSnifferRunning()) {
                MESSAGE_RECEIVER.start()
                if (!WaitUtil.waitUntil({ MESSAGE_RECEIVER.isSnifferRunning() })) {
                    error("Couldn't start sniffer")
                }
            }
        }
    }

    private fun updateCurrentConnections() {
        val findConnectionsProcessBuilder = ProcessBuilder(NETSTAT_COMMAND.split(" "))
        val process = findConnectionsProcessBuilder.start()
        if (process.waitFor(5L, TimeUnit.SECONDS)) {
            val runningConnections = BufferedReader(InputStreamReader(process.inputStream)).readLines()
                .mapNotNull { NETSTAT_DOFUS_REGEX.matchEntire(it) }
                .map { parseDofusConnection(it) }
            val storedConnections = lock.executeSyncOperation {
                CONNECTIONS_BY_CHARACTER_NAME.values.flatten()
            }
            stopListeningToDeadConnections(storedConnections.filter { !runningConnections.contains(it) })
            listenToConnections(runningConnections.filter { !storedConnections.contains(it) })
        }
    }

    private fun parseDofusConnection(matchResult: MatchResult): DofusConnection {
        return DofusConnection(
            Host(matchResult.groupValues[1].trim(), matchResult.groupValues[2].trim()),
            Host(matchResult.groupValues[3].trim(), matchResult.groupValues[4].trim()),
            matchResult.groupValues[5].trim().toLong()
        )
    }

    private fun stopListeningToDeadConnections(deadConnections: List<DofusConnection>) {
        val removedCharacters = HashSet<DofusCharacter>()
        for (connection in deadConnections) {
            MESSAGE_RECEIVER.stopListening(connection.client)
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
            CONNECTIONS_BY_CHARACTER_NAME.entries.filter { it.value.contains(connection) }
                .forEach { it.value.remove(connection) }
            val toRemove = CONNECTIONS_BY_CHARACTER_NAME.entries.filter { it.value.isEmpty() }
            CONNECTIONS_BY_CHARACTER_NAME.entries.removeAll(toRemove.toSet())
            CONNECTIONS_BY_GAME_INFO.entries.filter { it.value.contains(connection) }
                .forEach { it.value.remove(connection) }
            CONNECTIONS_BY_GAME_INFO.entries.removeIf { it.value.isEmpty() }
            toRemove.mapNotNull { CharacterManager.getCharacter(it.key) }
        }
    }

    private fun listenToConnections(newConnections: List<DofusConnection>) {
        for (connection in newConnections) {
            val characterName = getCharacterNameFromFrame(connection.pid) ?: continue
            val character = CharacterManager.getCharacter(characterName)
                ?: CharacterManager.addCharacter(characterName, 1, emptyList())
            val gameInfo = CONNECTIONS_BY_GAME_INFO.keys.firstOrNull { it.character == character }
                ?: GameInfo(character).also { it.connection = connection }
            listenToConnection(gameInfo, character, connection)
        }
    }

    private fun listenToConnection(gameInfo: GameInfo, character: DofusCharacter, connection: DofusConnection) {
        gameInfo.shouldInitBoard = true
        MESSAGE_RECEIVER.startListening(connection, gameInfo.eventStore, character.snifferLogger)
        lock.executeSyncOperation {
            CONNECTIONS_BY_CHARACTER_NAME.computeIfAbsent(character.name) { ArrayList() }.add(connection)
            CONNECTIONS_BY_GAME_INFO.computeIfAbsent(gameInfo) { ArrayList() }.add(connection)
        }
        println("start listening (${GlobalConfigManager.readConfig().networkInterfaceName}) : ${character.name} ($connection)")
        getListeners(character).forEach { listener ->
            listener.onListenStart(character)
        }
    }

    private fun getCharacterNameFromFrame(pid: Long): String? {
        return FRAME_NAME_REGEX.matchEntire(JNAUtil.getFrameName(pid))?.destructured?.component1()
    }

}