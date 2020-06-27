package fr.lewon.dofus.bot.scripts.tasks.impl

import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.Directions
import fr.lewon.dofus.bot.util.GameInfoUtil

class RetrieveNextDirTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?
) : DofusBotTask<Directions>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Directions {
        return GameInfoUtil.getNextDirection(controller.captureGameImage())
            ?: throw Exception()
    }

    override fun onFailed(exception: Exception, logItem: LogItem) {
        controller.closeLog("KO", logItem)
    }

    override fun onSucceeded(value: Directions, logItem: LogItem) {
        controller.closeLog("OK : $value", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Fetching next objective direction ...", parentLogItem)
    }
}