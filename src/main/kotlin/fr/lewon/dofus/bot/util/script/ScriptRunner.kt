package fr.lewon.dofus.bot.util.script

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.gui2.util.SoundType
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.VldbScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.tasks.impl.init.InitAllTask
import fr.lewon.dofus.bot.util.ListenableByCharacter
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.jna.JNAUtil
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

    override fun onCharacterMove(character: DofusCharacter, toIndex: Int) {
        // Nothing
    }

    override fun onCharacterDelete(character: DofusCharacter) {
        removeListeners(character)
    }

    @Synchronized
    fun runScript(character: DofusCharacter, scriptBuilder: DofusBotScriptBuilder, scriptValues: VldbScriptValues) {
        if (isScriptRunning(character)) {
            error("Cannot run script, there is already one running")
        }
        val logger = character.executionLogger
        getListeners(character.pseudo).forEach { it.onScriptStart(character, scriptBuilder) }
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
        RUNNING_SCRIPT_BY_CHARACTER_NAME[character.pseudo] = RunningScript(scriptBuilder, thread)
        thread.start()
    }

    fun isScriptRunning(character: DofusCharacter): Boolean {
        return RUNNING_SCRIPT_BY_CHARACTER_NAME[character.pseudo]?.thread?.isAlive == true
    }

    fun getRunningScript(character: DofusCharacter): RunningScript? {
        return RUNNING_SCRIPT_BY_CHARACTER_NAME[character.pseudo]
            ?.takeIf { it.thread.isAlive }
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
        RUNNING_SCRIPT_BY_CHARACTER_NAME.remove(character.pseudo)?.thread?.interrupt()
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
        getListeners(character.pseudo).forEach { it.onScriptEnd(character, endType) }
    }

    class RunningScript(
        val scriptBuilder: DofusBotScriptBuilder,
        val thread: Thread,
        val startTime: Long = System.currentTimeMillis()
    )
}