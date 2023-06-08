package fr.lewon.dofus.bot.util.script

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.gui.util.SoundType
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.tasks.impl.init.InitAllTask
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.listenable.ListenableByCharacter
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object ScriptRunner : ListenableByCharacter<ScriptRunnerListener>(), CharacterManagerListener {

    private val RUNNING_SCRIPT_BY_CHARACTER_NAME = HashMap<String, RunningScript>()

    init {
        CharacterManager.addListener(this)
    }

    override fun onCharacterCreate(character: DofusCharacter) {
        // Nothing
    }

    override fun onCharacterDelete(character: DofusCharacter) {
        // Nothing
    }

    override fun onCharacterUpdate(character: DofusCharacter) {
        // Nothing
    }

    @Synchronized
    fun runScript(character: DofusCharacter, scriptBuilder: DofusBotScriptBuilder, scriptValues: ScriptValues) {
        if (isScriptRunning(character)) {
            error("Cannot run script, there is already one running")
        }
        val logger = character.executionLogger
        val logItem = logger.log("Executing Dofus script : [${scriptBuilder.name}]")
        val thread = Thread {
            try {
                val gameInfo = prepareScriptExecution(character, logItem)
                gameInfo.eventStore.clear()
                val script = scriptBuilder.buildScript()
                script.execute(logItem, gameInfo, scriptValues)
                onScriptOk(character, logItem)
            } catch (e: InterruptedException) {
                onScriptCanceled(character, logItem)
            } catch (e: IllegalMonitorStateException) {
                onScriptCanceled(character, logItem)
            } catch (t: Throwable) {
                onScriptKo(character, t, logItem)
            }
        }
        val runningScript = RunningScript(scriptBuilder, thread)
        RUNNING_SCRIPT_BY_CHARACTER_NAME[character.name] = runningScript
        getListeners(character.name).forEach { it.onScriptStart(character, runningScript) }
        thread.start()
    }

    fun isScriptRunning(character: DofusCharacter): Boolean {
        return RUNNING_SCRIPT_BY_CHARACTER_NAME[character.name]?.thread?.isAlive == true
    }

    fun getRunningScript(character: DofusCharacter): RunningScript? {
        return RUNNING_SCRIPT_BY_CHARACTER_NAME[character.name]
            ?.takeIf { it.thread.isAlive }
    }

    private fun prepareScriptExecution(currentCharacter: DofusCharacter, logItem: LogItem): GameInfo {
        GameSnifferUtil.updateNetwork()
        val connection = GameSnifferUtil.getFirstConnection(currentCharacter) ?: error("Character must be logged in")
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        JNAUtil.updateGameBounds(gameInfo)

        if (gameInfo.shouldInitBoard) {
            if (!InitAllTask().run(logItem, gameInfo)) {
                error("Failed to init bot")
            }
            gameInfo.shouldInitBoard = false
        }
        return gameInfo
    }

    @Synchronized
    fun stopScript(characterName: String) {
        RUNNING_SCRIPT_BY_CHARACTER_NAME.remove(characterName)?.thread?.interrupt()
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
        getListeners(character.name).forEach { it.onScriptEnd(character, endType) }
    }

    class RunningScript(
        val scriptBuilder: DofusBotScriptBuilder,
        val thread: Thread,
        val startTime: Long = System.currentTimeMillis()
    )
}