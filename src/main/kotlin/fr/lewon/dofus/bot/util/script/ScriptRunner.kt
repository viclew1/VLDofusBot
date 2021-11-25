package fr.lewon.dofus.bot.util.script

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.tasks.impl.init.InitAllTask
import fr.lewon.dofus.bot.scripts.tasks.impl.windows.RestartGameTask
import fr.lewon.dofus.bot.util.JNAUtil
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
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
                dofusScript.execute(currentLogItem, gameInfo, cancellationToken)
                onScriptOk()
            } catch (e: Exception) {
                if (cancellationToken.cancel) {
                    onScriptCanceled()
                } else {
                    onScriptKo(e)
                }
            }
        }.also { it.start() }
        listeners.forEach { it.onScriptStart(dofusScript) }
    }

    private fun prepareScriptExecution(): GameInfo {
        val pid = getDofusPID()
        val gameInfo = GameSnifferUtil.getGameInfoByPID(pid)
        JNAUtil.updateGameBounds(gameInfo, pid)

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
        var dofusConnection = GameSnifferUtil.getCharacterPID(currentCharacter)
        if (dofusConnection == null) {
            dofusConnection = RestartGameTask().run(connectionLog, GameInfo(currentCharacter), cancellationToken)
        }
        VldbLogger.closeLog("OK", connectionLog)
        return dofusConnection
    }

    @Synchronized
    fun stopScript() {
        runnerThread?.takeIf { it.isAlive }?.let {
            cancellationToken.cancel = true
            it.interrupt()
        }
    }

    private fun onScriptKo(e: Exception) {
        VldbLogger.closeLog("Execution KO - ${e.localizedMessage}", currentLogItem)
        e.printStackTrace()
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