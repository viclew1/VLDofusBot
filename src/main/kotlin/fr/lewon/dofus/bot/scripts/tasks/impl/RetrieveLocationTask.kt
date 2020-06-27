package fr.lewon.dofus.bot.scripts.tasks.impl

import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.GameInfoUtil

class RetrieveLocationTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?
) : DofusBotTask<Pair<Int, Int>>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Pair<Int, Int> {
        return GameInfoUtil.getLocation(controller.captureGameImage())
            ?: throw Exception()
    }

    override fun onFailed(exception: Exception, logItem: LogItem) {
        controller.closeLog("KO", logItem)
    }

    override fun onSucceeded(value: Pair<Int, Int>, logItem: LogItem) {
        controller.closeLog("OK - [${value.first},${value.second}]", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Retrieving current location ...", parentLogItem)
    }

}