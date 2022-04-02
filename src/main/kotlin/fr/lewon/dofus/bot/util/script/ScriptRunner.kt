package fr.lewon.dofus.bot.util.script

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.gui.alert.SoundType
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.tasks.impl.init.InitAllTask
import fr.lewon.dofus.bot.util.filemanagers.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object ScriptRunner : CharacterManagerListener {

    private val listenersByCharacterName = HashMap<String, ArrayList<ScriptRunnerListener>>()
    private val runningScriptByCharacterName = HashMap<String, Thread>()

    fun addListener(character: DofusCharacter, listener: ScriptRunnerListener) {
        val characterListeners = listenersByCharacterName.computeIfAbsent(character.pseudo) { ArrayList() }
        characterListeners.add(listener)
    }

    fun removeListener(character: DofusCharacter, listener: ScriptRunnerListener) {
        listenersByCharacterName[character.pseudo]?.remove(listener)
    }

    fun removeListener(listener: ScriptRunnerListener) {
        listenersByCharacterName.forEach { it.value.remove(listener) }
    }

    override fun onCharacterCreate(character: DofusCharacter) {
        // Nothing
    }

    override fun onCharacterMove(character: DofusCharacter, toIndex: Int) {
        // Nothing
    }

    override fun onCharacterDelete(character: DofusCharacter) {
        listenersByCharacterName.remove(character.pseudo)
    }

    @Synchronized
    fun runScript(character: DofusCharacter, dofusScript: DofusBotScript) {
        if (isScriptRunning(character)) {
            error("Cannot run script, there is already one running")
        }
        val logger = character.executionLogger
        listenersByCharacterName[character.pseudo]?.forEach { it.onScriptStart(character, dofusScript) }
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

    fun isScriptRunning(character: DofusCharacter): Boolean {
        return runningScriptByCharacterName[character.pseudo]?.isAlive == true
    }

    private fun prepareScriptExecution(currentCharacter: DofusCharacter, logItem: LogItem): GameInfo {
        GameSnifferUtil.updateNetwork()
        val connection = GameSnifferUtil.getFirstConnection(currentCharacter) ?: error("Character must be logged in")
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        JNAUtil.updateGameBounds(gameInfo)

        if (gameInfo.shouldInitBoard) {
            InitAllTask().run(logItem, gameInfo)
            gameInfo.shouldInitBoard = false
        }
        return gameInfo
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
        listenersByCharacterName[character.pseudo]?.forEach { it.onScriptEnd(character, endType) }
    }
}