package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.GameInfoUtil
import fr.lewon.dofus.bot.util.WindowsUtil

object CleanCacheScript : DofusBotScript("Clean cache") {

    override fun getParameters(): List<DofusBotScriptParameter> {
        return emptyList()
    }

    override fun getStats(): List<Pair<String, String>> {
        return emptyList()
    }

    override fun getDescription(): String {
        return "Restarts the game, helps with getting rid of the memory saturation occurring after the game being opened for a while"
    }

    override fun doExecute(
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?,
        parameters: Map<String, DofusBotScriptParameter>
    ) {
        controller.log("Accessing cache options.", logItem)
        execTimeoutOpe({ pressShortcut('o') }, { imgFound("cache_option.png", 0.9) })
        clickChain(listOf("cache_option.png"), "clean_cache_button.png")

        val cleanLog = controller.log("Cleaning cache ...", logItem)
        clickChain(listOf("clean_cache_button.png", "ok_cancel.png"))
        execTimeoutOpe({ }, { !WindowsUtil.isGameOpen() })
        controller.closeLog("OK", cleanLog)

        val reopenLog = controller.log("Waiting for the Ankama Launcher the relaunch the game ...", logItem)
        execTimeoutOpe({ }, { WindowsUtil.isGameOpen() }, timeOutSeconds = 180)
        controller.closeLog("OK", reopenLog)

        val readyLog = controller.log("Waiting for the character to be logged in ...", logItem)
        sleep(25000)
        execTimeoutOpe(
            { WindowsUtil.bringGameToFront(controller.getGameScreen()) },
            { GameInfoUtil.getLocation(controller.captureGameImage()) != null },
            timeOutSeconds = 180
        )
        controller.closeLog("OK", readyLog)
    }

}