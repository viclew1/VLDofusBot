package fr.lewon.dofus.bot.scripts.tasks.impl.complex

import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.Directions
import fr.lewon.dofus.bot.util.GameInfoUtil

class MultimapPhorrorSearchTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?,
    private val direction: Directions,
    private val alreadyFoundPos: List<Pair<Int, Int>>
) : DofusBotTask<Pair<Int, Int>>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Pair<Int, Int> {
        var currentDir = direction
        var mapsToPass = 10
        for (tryCount in 1..4) {
            for (moveCount in 0 until mapsToPass) {
                try {
                    val newPos = currentDir.buildMoveTask(controller, logItem).run()
                    if (!alreadyFoundPos.contains(newPos) && GameInfoUtil.phorrorOnMap(controller.captureGameImage())) {
                        return newPos
                    }
                } catch (e: Exception) {
                    mapsToPass = moveCount
                    if (mapsToPass == 0) throw e
                    break
                }
            }
            currentDir = currentDir.getReverseDir()
        }
        throw Exception("No phorror found")
    }

    override fun onFailed(exception: Exception, logItem: LogItem) {
        controller.closeLog("KO - ${exception.localizedMessage}", logItem)
    }

    override fun onSucceeded(value: Pair<Int, Int>, logItem: LogItem) {
        controller.closeLog("Found : [${value.first},${value.second}]", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Looking for a phorror in the [$direction] direction ...", parentLogItem)
    }
}