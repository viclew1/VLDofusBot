package fr.lewon.dofus.bot.util.network

import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiver
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.jna.JNAUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.locks.ReentrantLock


object GameSnifferUtil {

    private const val NETSTAT_COMMAND = "cmd.exe /c netstat -aop TCP -n | findstr :5555"
    private val NETSTAT_DOFUS_REGEX = Regex(
        "[\\s]+TCP" +
                "[\\s]+([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+):([0-9]+)" +
                "[\\s]+([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+):(5555)" +
                "[\\s]+ESTABLISHED" +
                "[\\s]+([0-9]+)"
    )
    private val FRAME_NAME_REGEX = Regex("(.*?) - Dofus.*?")

    private val lock = ReentrantLock()

    private val connectionsByCharacterName = HashMap<String, ArrayList<DofusConnection>>()
    private val connectionsByGameInfo = HashMap<GameInfo, ArrayList<DofusConnection>>()
    var messageReceiver = DofusMessageReceiver(ConfigManager.config.networkInterfaceName)

    fun updateNetworkInterface() {
        try {
            lock.lockInterruptibly()

            messageReceiver.interrupt()
            messageReceiver.join()

            connectionsByCharacterName.clear()
            connectionsByGameInfo.clear()

            messageReceiver = DofusMessageReceiver(ConfigManager.config.networkInterfaceName)
            messageReceiver.start()
        } finally {
            lock.unlock()
        }
    }

    fun updateNetwork() {
        try {
            lock.lockInterruptibly()
            startSnifferIfNeeded()
            updateCurrentConnections()
        } finally {
            lock.unlock()
        }
    }

    private fun startSnifferIfNeeded() {
        if (!messageReceiver.isSnifferRunning()) {
            messageReceiver.start()
            WaitUtil.waitUntil({ messageReceiver.isSnifferRunning() })
        }
    }

    private fun updateCurrentConnections() {
        val runningConnections = getRunningConnections()
        val storedConnections = connectionsByCharacterName.values.flatten()
        stopListeningToDeadConnections(storedConnections.filter { !runningConnections.contains(it) })
        listenToConnections(runningConnections.filter { !storedConnections.contains(it) })
    }

    private fun getRunningConnections(): List<DofusConnection> {
        val findServerIpProcessBuilder = ProcessBuilder(NETSTAT_COMMAND.split(" "))
        val process = findServerIpProcessBuilder.start()
        process.waitFor()
        return BufferedReader(InputStreamReader(process.inputStream)).readLines()
            .mapNotNull { NETSTAT_DOFUS_REGEX.matchEntire(it) }
            .map { parseDofusConnection(it) }
    }

    private fun parseDofusConnection(matchResult: MatchResult): DofusConnection {
        return DofusConnection(
            matchResult.groupValues[1].trim(),
            matchResult.groupValues[2].trim(),
            matchResult.groupValues[3].trim(),
            matchResult.groupValues[4].trim(),
            matchResult.groupValues[5].trim().toLong()
        )
    }

    private fun stopListeningToDeadConnections(deadConnections: List<DofusConnection>) {
        for (connection in deadConnections) {
            messageReceiver.stopListening(connection.hostPort)
            connectionsByCharacterName.entries.filter { it.value.contains(connection) }
                .forEach { it.value.remove(connection) }
            connectionsByCharacterName.entries.removeIf { it.value.isEmpty() }
            connectionsByGameInfo.entries.filter { it.value.contains(connection) }
                .forEach { it.value.remove(connection) }
            connectionsByGameInfo.entries.removeIf { it.value.isEmpty() }
            println("stop listening (${ConfigManager.config.networkInterfaceName}) : $connection")
        }
    }

    private fun listenToConnections(newConnections: List<DofusConnection>) {
        for (connection in newConnections) {
            val characterName = getCharacterNameFromFrame(connection.pid) ?: continue
            val character = CharacterManager.getCharacterByName(characterName) ?: continue
            val gameInfo = connectionsByGameInfo.keys.firstOrNull { it.character == character }
                ?: GameInfo(character).also { it.connection = connection }
            listenToConnection(gameInfo, character, connection)
        }
    }

    private fun listenToConnection(gameInfo: GameInfo, character: DofusCharacter, connection: DofusConnection) {
        messageReceiver.startListening(connection, gameInfo.eventStore, character.snifferLogger)
        connectionsByCharacterName.computeIfAbsent(character.pseudo) { ArrayList() }.add(connection)
        connectionsByGameInfo.computeIfAbsent(gameInfo) { ArrayList() }.add(connection)
        println("start listening (${ConfigManager.config.networkInterfaceName}) : ${character.pseudo} ($connection)")
    }

    private fun getCharacterNameFromFrame(pid: Long): String? {
        return FRAME_NAME_REGEX.matchEntire(JNAUtil.getFrameName(pid))?.destructured?.component1()
    }

    fun getGameInfoByConnection(connection: DofusConnection): GameInfo {
        try {
            lock.lockInterruptibly()
            return connectionsByGameInfo.entries.firstOrNull { it.value.contains(connection) }?.key
                ?: error("There is no game info associated to connection : ${connection.pid}")
        } finally {
            lock.unlock()
        }
    }

    fun getFirstConnection(character: DofusCharacter): DofusConnection? {
        return getConnections(character).firstOrNull()
    }

    fun getConnections(character: DofusCharacter): List<DofusConnection> {
        try {
            lock.lockInterruptibly()
            return connectionsByCharacterName[character.pseudo] ?: emptyList()
        } finally {
            lock.unlock()
        }
    }

}