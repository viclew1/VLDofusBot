package fr.lewon.dofus.bot.util.ui

import fr.lewon.dofus.bot.gui.MainFrame
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiver
import fr.lewon.dofus.bot.util.WindowsUtil
import fr.lewon.dofus.bot.util.logs.LogItem
import fr.lewon.dofus.bot.util.logs.VldbLogger
import java.awt.Color

object ScriptRunner {

    private var isThreadRunning = false
    private lateinit var runnerThread: Thread
    private lateinit var currentLogItem: LogItem

    @Synchronized
    fun runScript(dofusScript: DofusBotScript) {
        if (isThreadRunning) {
            error("Cannot run script, there is already one running")
        }
        runnerThread = Thread {
            currentLogItem = VldbLogger.log("Executing Dofus script : [${dofusScript.name}]")
            try {
                if (!DofusMessageReceiver.isThreadAlive()) {
                    DofusMessageReceiver.restartThread()
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
        MainFrame.loading()
        isThreadRunning = true
    }

    @Synchronized
    fun stopScript() {
        if (isThreadRunning) {
            runnerThread.interrupt()
            onScriptCanceled()
        }
    }

    private fun onScriptKo(e: Exception) {
        VldbLogger.closeLog("Execution KO - ${e.localizedMessage}", currentLogItem)
        e.printStackTrace()
        onScriptEnd(Color.RED)
    }

    private fun onScriptCanceled() {
        VldbLogger.closeLog("Execution canceled", currentLogItem)
        onScriptEnd(Color.ORANGE)
    }

    private fun onScriptOk() {
        VldbLogger.closeLog("Execution OK", currentLogItem)
        onScriptEnd(Color.GREEN)
    }

    private fun onScriptEnd(progressBarColor: Color) {
        MainFrame.stopLoading(progressBarColor)
        isThreadRunning = false
    }
}