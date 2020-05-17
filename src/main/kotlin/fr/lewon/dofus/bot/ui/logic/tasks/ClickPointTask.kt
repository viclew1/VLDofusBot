package fr.lewon.dofus.bot.ui.logic.tasks

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.ui.logic.DofusBotTask
import fr.lewon.dofus.bot.util.DTBConfigManager
import fr.lewon.dofus.bot.util.RobotUtil
import javafx.concurrent.WorkerStateEvent

class ClickPointTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?,
    private var x: Int,
    private var y: Int
) : DofusBotTask<Boolean>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Boolean {
        val screenBounds = controller.getGameScreen().defaultConfiguration.bounds
        val restPos = DTBConfigManager.config.mouseRestPos
        RobotUtil.click(
            screenBounds.x + x,
            screenBounds.y + y
        )
        RobotUtil.move(
            screenBounds.x + restPos.first,
            screenBounds.y + restPos.second
        )
        return true
    }

    override fun onFailed(event: WorkerStateEvent, logItem: LogItem) {
        controller.closeLog("KO - ${event.source.exception.localizedMessage}", logItem)
    }

    override fun onSucceeded(event: WorkerStateEvent, value: Boolean, logItem: LogItem) {
        controller.closeLog("OK", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Clicking [$x,$y] point ... ", parentLogItem)
    }
}