package fr.lewon.dofus.bot.ui.logic.tasks.complex

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.ui.logic.DofusBotTask
import fr.lewon.dofus.bot.ui.logic.tasks.ClickButtonTask
import fr.lewon.dofus.bot.util.DofusImages
import fr.lewon.dofus.bot.util.GameInfoUtil
import fr.lewon.dofus.bot.util.RobotUtil
import javafx.concurrent.WorkerStateEvent

class ReachMapTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?,
    private val x: Int,
    private val y: Int
) : DofusBotTask<Pair<Int, Int>>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Pair<Int, Int> {
        ClickButtonTask(controller, logItem, DofusImages.CHAT.path).runAndGet()
        RobotUtil.write("/travel $x $y")
        Thread.sleep(500)
        RobotUtil.write("")
        val startTimeMillis = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTimeMillis < 300 * 1000) {
            Thread.sleep(2000)
            val newLocation = GameInfoUtil.getLocation(controller.captureGameImage())
            if (newLocation != null && newLocation.first == x && newLocation.second == y) {
                return newLocation
            }
        }
        throw Exception("Move timeout")
    }

    override fun onFailed(event: WorkerStateEvent, logItem: LogItem) {
        controller.closeLog("KO - ${event.source.exception.localizedMessage}", logItem)
    }

    override fun onSucceeded(event: WorkerStateEvent, value: Pair<Int, Int>, logItem: LogItem) {
        controller.closeLog("OK : [${value.first},${value.second}]", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Moving toward [$x, $y] position ...", parentLogItem)
    }

}