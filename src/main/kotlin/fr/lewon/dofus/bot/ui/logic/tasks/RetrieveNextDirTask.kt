package fr.lewon.dofus.bot.ui.logic.tasks

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.ui.logic.DofusBotTask
import fr.lewon.dofus.bot.util.Directions
import fr.lewon.dofus.bot.util.GameInfoUtil
import javafx.concurrent.WorkerStateEvent

class RetrieveNextDirTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?
) : DofusBotTask<Directions>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Directions {
        return GameInfoUtil.getNextDirection(controller.captureGameImage())
            ?: throw Exception()
    }

    override fun onFailed(event: WorkerStateEvent, logItem: LogItem) {
        controller.closeLog("KO", logItem)
    }

    override fun onSucceeded(event: WorkerStateEvent, value: Directions, logItem: LogItem) {
        controller.closeLog("OK : $value", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Fetching next objective direction ...", parentLogItem)
    }
}