package fr.lewon.dofus.bot.util.script

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.gui.sound.SoundType
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.tasks.impl.init.InitAllTask
import fr.lewon.dofus.bot.scripts.tasks.impl.windows.RestartGameTask
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object ScriptRunner {

    private var runnerThread: Thread? = null
    private lateinit var currentLogItem: LogItem
    private lateinit var cancellationToken: CancellationToken
    val listeners = ArrayList<ScriptRunnerListener>()

    @Synchronized
    fun runScript(dofusScript: DofusBotScript) {
        if (runnerThread?.isAlive == true) {
            error("Cannot run script, there is already one running")
        }
        runnerThread = Thread {
            currentLogItem = VldbLogger.info("Executing Dofus script : [${dofusScript.name}]")
            cancellationToken = CancellationToken()
            try {
                val gameInfo = prepareScriptExecution()
                EventStore.clear(gameInfo.snifferId)
                dofusScript.execute(currentLogItem, gameInfo, cancellationToken)
                onScriptOk()
            } catch (t: Throwable) {
                if (cancellationToken.cancel) {
                    onScriptCanceled()
                } else {
                    onScriptKo(t)
                }
            }
        }.also { it.start() }
        listeners.forEach { it.onScriptStart(dofusScript) }
    }

    private fun prepareScriptExecution(): GameInfo {
        val pid = getDofusPID()
        val gameInfo = GameSnifferUtil.getGameInfoByPID(pid)
        JNAUtil.updateGameBounds(gameInfo)

        if (gameInfo.shouldInitBoard) {
            InitAllTask().run(currentLogItem, gameInfo, cancellationToken)
            gameInfo.shouldInitBoard = false
        }
        return gameInfo
    }

    private fun getDofusPID(): Long {
        val connectionLog = VldbLogger.info("Fetching dofus PID ...", currentLogItem)
        val currentCharacter = CharacterManager.getCurrentCharacter()
            ?: error("There should be a selected character")
        var pid = GameSnifferUtil.getCharacterPID(currentCharacter)
        if (pid == null) {
            pid = RestartGameTask().run(connectionLog, GameInfo(currentCharacter), cancellationToken)
        }
        VldbLogger.closeLog("OK", connectionLog)
        return pid
    }

    @Synchronized
    fun stopScript() {
        runnerThread?.takeIf { it.isAlive }?.let {
            cancellationToken.cancel = true
            it.interrupt()
        }
    }

    private fun onScriptKo(t: Throwable) {
        VldbLogger.closeLog("Execution KO - ${t.localizedMessage}", currentLogItem)
        t.printStackTrace()
        SoundType.FAILED.playSound()
        onScriptEnd(DofusBotScriptEndType.FAIL)
    }

    private fun onScriptCanceled() {
        VldbLogger.closeLog("Execution canceled", currentLogItem)
        onScriptEnd(DofusBotScriptEndType.CANCEL)
    }

    private fun onScriptOk() {
        VldbLogger.closeLog("Execution OK", currentLogItem)
        onScriptEnd(DofusBotScriptEndType.SUCCESS)
    }

    private fun onScriptEnd(endType: DofusBotScriptEndType) {
        listeners.forEach { it.onScriptEnd(endType) }
    }
}