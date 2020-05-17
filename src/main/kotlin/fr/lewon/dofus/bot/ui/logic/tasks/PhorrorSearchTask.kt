package fr.lewon.dofus.bot.ui.logic.tasks

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.ui.logic.DofusBotTask
import fr.lewon.dofus.bot.util.GameInfoUtil
import javafx.concurrent.WorkerStateEvent

class PhorrorSearchTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?
) : DofusBotTask<Boolean>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Boolean {
        return GameInfoUtil.phorrorOnMap(controller.captureGameImage())
    }

    override fun onFailed(event: WorkerStateEvent, logItem: LogItem) {
        controller.closeLog("KO - ${event.source.exception.localizedMessage}", logItem)
    }

    override fun onSucceeded(event: WorkerStateEvent, value: Boolean, logItem: LogItem) {
        if (value) controller.closeLog("Found", logItem)
        else controller.closeLog("Not found", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Looking for a phorror ...", parentLogItem)
    }

}