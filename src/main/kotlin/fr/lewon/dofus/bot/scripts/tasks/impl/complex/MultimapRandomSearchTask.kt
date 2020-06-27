package fr.lewon.dofus.bot.scripts.tasks.impl.complex

import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.ClickButtonTask
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.Directions
import fr.lewon.dofus.bot.util.DofusImages
import fr.lewon.dofus.bot.util.GameInfoUtil

class MultimapRandomSearchTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?,
    private val direction: Directions
) : DofusBotTask<Pair<Int, Int>>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Pair<Int, Int> {
        var objectsToFind = getObjectsToFind()
        val originalObjectsToFindSize = objectsToFind.size
        var pos: Pair<Int, Int>? = null
        while (objectsToFind.size == originalObjectsToFindSize) {
            pos = direction.buildMoveTask(controller, logItem).run()
            ClickButtonTask(
                controller,
                logItem,
                DofusImages.CHECKPOINT_BTN.path
            ).run()
            Thread.sleep(800)
            ClickButtonTask(
                controller,
                logItem,
                DofusImages.SEARCH_BTN.path
            ).run()
            Thread.sleep(800)
            objectsToFind = getObjectsToFind()
        }
        if (pos == null) {
            error("Invalid move")
        }
        return pos
    }

    private fun getObjectsToFind(): List<String> {
        val gameImage = controller.captureGameImage()
        val huntPanel = GameInfoUtil.getHuntPanel(gameImage)
            ?: throw Exception("Failed to retrieve hunt panel")
        return GameInfoUtil.getHuntObjectives(huntPanel)
    }

    override fun onFailed(exception: Exception, logItem: LogItem) {
        controller.closeLog("KO - ${exception.localizedMessage}", logItem)
    }

    override fun onSucceeded(value: Pair<Int, Int>, logItem: LogItem) {
        controller.closeLog("OK : [${value.first},${value.second}]", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Moving in the [$direction] direction in hope to find hint ...", parentLogItem)
    }

}