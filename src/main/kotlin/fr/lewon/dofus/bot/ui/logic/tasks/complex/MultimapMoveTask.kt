package fr.lewon.dofus.bot.ui.logic.tasks.complex

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.ui.logic.DofusBotTask
import fr.lewon.dofus.bot.util.Directions
import javafx.concurrent.WorkerStateEvent

class MultimapMoveTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?,
    private val direction: Directions,
    private val dist: Int
) : DofusBotTask<Pair<Int, Int>>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Pair<Int, Int> {
        var currentPos: Pair<Int, Int>? = null
        for (i in 0 until dist) {
            controller.closeLog("Moves done : $i/$dist", logItem)
            currentPos = direction.buildMoveTask(controller, logItem).runAndGet()
        }
        return currentPos ?: error("Invalid move")
    }

    override fun onFailed(event: WorkerStateEvent, logItem: LogItem) {
        controller.closeLog("KO - ${event.source.exception.localizedMessage}", logItem)
    }

    override fun onSucceeded(event: WorkerStateEvent, value: Pair<Int, Int>, logItem: LogItem) {
        controller.closeLog("OK : [${value.first},${value.second}]", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Moving [$dist] cells in the [$direction] direction ...", parentLogItem)
    }

}