package fr.lewon.dofus.bot.scripts.tasks.impl.complex

import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.Directions

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
            currentPos = direction.buildMoveTask(controller, logItem).run()
        }
        return currentPos ?: error("Invalid move")
    }

    override fun onFailed(exception: Exception, logItem: LogItem) {
        controller.closeLog("KO - ${exception.localizedMessage}", logItem)
    }

    override fun onSucceeded(value: Pair<Int, Int>, logItem: LogItem) {
        controller.closeLog("OK : [${value.first},${value.second}]", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Moving [$dist] cells in the [$direction] direction ...", parentLogItem)
    }

}