package fr.lewon.dofus.bot.util.script

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiver
import fr.lewon.dofus.bot.util.WindowsUtil

object ScriptRunner {

    private var isThreadRunning = false
    private lateinit var runnerThread: Thread
    private lateinit var currentLogItem: LogItem
    val listeners = ArrayList<ScriptRunnerListener>()

    @Synchronized
    fun runScript(dofusScript: DofusBotScript) {
        if (isThreadRunning) {
            error("Cannot run script, there is already one running")
        }
        runnerThread = Thread {
            currentLogItem = VldbLogger.info("Executing Dofus script : [${dofusScript.name}]")
            try {
                if (!DofusMessageReceiver.isThreadAlive()) {
                    DofusMessageReceiver.killAndStartThread()
                }
                WindowsUtil.bringGameToFront()
                WindowsUtil.updateGameBounds()
                dofusScript.execute(currentLogItem)
                onScriptOk()
            } catch (e: Exception) {
                onScriptKo(e)
            }
        }
        runnerThread.start()
        listeners.forEach { it.onScriptStart(dofusScript) }
        isThreadRunning = true
    }

    @Synchronized
    fun stopScript() {
        if (isThreadRunning) {
            runnerThread.stop()
            onScriptCanceled()
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
        isThreadRunning = false
    }
}