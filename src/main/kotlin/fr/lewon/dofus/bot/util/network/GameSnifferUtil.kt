package fr.lewon.dofus.bot.util.network

import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiver
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
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

    private val connectionByCharacterName = HashMap<String, DofusConnection>()
    private val gameInfoByConnection = HashMap<DofusConnection, GameInfo>()
    private val messageReceiver = DofusMessageReceiver()

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
        stopListeningToDeadConnections(connectionByCharacterName.values.filter { !runningConnections.contains(it) })
        listenToConnections(runningConnections.filter { !connectionByCharacterName.values.contains(it) })
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
            val characterName = connectionByCharacterName.entries.firstOrNull { e -> e.value == connection }?.key
                ?: error("There shouldn't be a connection without a character")

            messageReceiver.stopListening(connection.hostPort)
            connectionByCharacterName.remove(characterName)
            gameInfoByConnection.remove(connection)
            println("stop listening : $characterName ($connection)")
        }
    }

    private fun listenToConnections(newConnections: List<DofusConnection>) {
        for (connection in newConnections) {
            val characterName = getCharacterNameFromFrame(connection.pid) ?: continue
            val character = CharacterManager.getCharacterByName(characterName) ?: continue
            val gameInfo = GameInfo(character).also { it.connection = connection }
            listenToConnection(gameInfo, character, connection)
        }
    }

    private fun listenToConnection(gameInfo: GameInfo, character: DofusCharacter, connection: DofusConnection) {
        messageReceiver.startListening(connection, gameInfo.eventStore, character.snifferLogger)
        connectionByCharacterName[character.pseudo] = connection
        gameInfoByConnection[connection] = gameInfo
        println("start listening : ${character.pseudo} ($connection)")
    }

    private fun getCharacterNameFromFrame(pid: Long): String? {
        return FRAME_NAME_REGEX.matchEntire(JNAUtil.getFrameName(pid))?.destructured?.component1()
    }

    fun getGameInfoByConnection(connection: DofusConnection): GameInfo {
        try {
            lock.lockInterruptibly()
            return gameInfoByConnection[connection]
                ?: error("There is no game info associated to connection : ${connection.pid}")
        } finally {
            lock.unlock()
        }
    }

    fun getConnection(character: DofusCharacter): DofusConnection? {
        try {
            lock.lockInterruptibly()
            return connectionByCharacterName[character.pseudo]
        } finally {
            lock.unlock()
        }
    }

    fun getAllPIDs(): List<Long> {
        try {
            lock.lockInterruptibly()
            return connectionByCharacterName.values.map { it.pid }
        } finally {
            lock.unlock()
        }
    }

    fun setGameInfo(connection: DofusConnection, gameInfo: GameInfo) {
        try {
            lock.lockInterruptibly()
            gameInfo.connection = connection
            val characterName = connectionByCharacterName.entries.firstOrNull { it.value == connection }?.key
                ?: error("No character for connection : $connection")
            val character = CharacterManager.getCharacterByName(characterName)
                ?: error("No character for name : $characterName")
            stopListeningToDeadConnections(listOf(connection))
            listenToConnection(gameInfo, character, connection)
        } finally {
            lock.unlock()
        }
    }

}