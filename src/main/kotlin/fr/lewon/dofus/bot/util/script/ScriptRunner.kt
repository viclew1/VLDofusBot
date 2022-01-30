package fr.lewon.dofus.bot.util.script

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.gui.alert.SoundType
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.tasks.impl.init.InitAllTask
import fr.lewon.dofus.bot.scripts.tasks.impl.windows.RestartGameTask
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object ScriptRunner {

    private val listenersByCharacterName = HashMap<String, ArrayList<ScriptRunnerListener>>()
    private val runningScriptByCharacterName = HashMap<String, Thread>()

    fun addListener(character: DofusCharacter, listener: ScriptRunnerListener) {
        val characterListeners = listenersByCharacterName.computeIfAbsent(character.pseudo) { ArrayList() }
        characterListeners.add(listener)
    }

    @Synchronized
    fun runScript(character: DofusCharacter, dofusScript: DofusBotScript) {
        if (runningScriptByCharacterName[character.pseudo]?.isAlive == true) {
            error("Cannot run script, there is already one running")
        }
        val logger = character.executionLogger
        listenersByCharacterName[character.pseudo]?.forEach { it.onScriptStart(dofusScript) }
        val logItem = logger.log("Executing Dofus script : [${dofusScript.name}]")
        val thread = Thread {
            try {
                val gameInfo = prepareScriptExecution(character, logItem)
                gameInfo.eventStore.clear()
                dofusScript.execute(logItem, gameInfo)
                onScriptOk(character, logItem)
            } catch (e: InterruptedException) {
                onScriptCanceled(character, logItem)
            } catch (e: IllegalMonitorStateException) {
                onScriptCanceled(character, logItem)
            } catch (t: Throwable) {
                onScriptKo(character, t, logItem)
            }
        }
        runningScriptByCharacterName[character.pseudo] = thread
        thread.start()
    }

    private fun prepareScriptExecution(currentCharacter: DofusCharacter, logItem: LogItem): GameInfo {
        GameSnifferUtil.updateNetwork()
        val connection = getDofusConnection(currentCharacter, logItem)
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        JNAUtil.updateGameBounds(gameInfo)

        if (gameInfo.shouldInitBoard) {
            InitAllTask().run(logItem, gameInfo)
            gameInfo.shouldInitBoard = false
        }
        return gameInfo
    }

    private fun getDofusConnection(character: DofusCharacter, logItem: LogItem): DofusConnection {
        val connectionLog = character.executionLogger.addSubLog("Fetching dofus connection ...", logItem)
        var connection = GameSnifferUtil.getConnection(character)
        if (connection == null) {
            connection = RestartGameTask().run(connectionLog, GameInfo(character))
        }
        character.executionLogger.closeLog("OK", connectionLog, true)
        return connection
    }

    @Synchronized
    fun stopScript(character: DofusCharacter) {
        runningScriptByCharacterName.remove(character.pseudo)?.interrupt()
    }

    private fun onScriptKo(character: DofusCharacter, t: Throwable, logItem: LogItem) {
        character.executionLogger.closeLog("Execution KO - ${t.localizedMessage}", logItem)
        SoundType.FAILED.playSound()
        t.printStackTrace()
        onScriptEnd(character, DofusBotScriptEndType.FAIL)
    }

    private fun onScriptCanceled(character: DofusCharacter, logItem: LogItem) {
        character.executionLogger.closeLog("Execution canceled", logItem)
        onScriptEnd(character, DofusBotScriptEndType.CANCEL)
    }

    private fun onScriptOk(character: DofusCharacter, logItem: LogItem) {
        character.executionLogger.closeLog("Execution OK", logItem)
        SoundType.SUCCEEDED.playSound()
        onScriptEnd(character, DofusBotScriptEndType.SUCCESS)
    }

    private fun onScriptEnd(character: DofusCharacter, endType: DofusBotScriptEndType) {
        listenersByCharacterName[character.pseudo]?.forEach { it.onScriptEnd(endType) }
    }
}