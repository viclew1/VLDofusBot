package fr.lewon.dofus.bot.ui.logic.tasks.complex

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.ui.logic.DofusBotTask
import fr.lewon.dofus.bot.ui.logic.tasks.ClickButtonTask
import fr.lewon.dofus.bot.util.DofusImages
import fr.lewon.dofus.bot.util.GameInfoUtil
import fr.lewon.dofus.bot.util.LevenshteinDistanceUtil
import javafx.concurrent.WorkerStateEvent

class ExecuteQuestTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?,
    private val hintsIdByName: MutableMap<String, List<String>>,
    private val altWorld: Boolean = false
) : DofusBotTask<Boolean>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Boolean {
        val questRegisteredPositions = ArrayList<Pair<Int, Int>>()
        var objectsToFind = emptyList<String>()
        while (true) {
            val gameImage = controller.captureGameImage()
            val huntPanel = GameInfoUtil.getHuntPanel(gameImage)
                ?: throw Exception("Failed to retrieve hunt panel")
            val newObjectsToFind = GameInfoUtil.getHuntObjectives(huntPanel)
            println(newObjectsToFind)

            if (objectsToFind.size == newObjectsToFind.size) {
                break
            }

            objectsToFind = newObjectsToFind

            val toFind =
                LevenshteinDistanceUtil.getClosestString(objectsToFind.last(), this.hintsIdByName.keys)
            controller.log("Next objective : $toFind", logItem)

            val toFindIds = hintsIdByName[toFind] ?: throw Exception("No ID for this object")
            val nextPos =
                ExecuteQuestStepTask(controller, logItem, questRegisteredPositions, toFindIds, altWorld).runAndGet()
            questRegisteredPositions.add(nextPos)

            ClickButtonTask(controller, logItem, DofusImages.CHECKPOINT_BTN.path).runAndGet()
            Thread.sleep(800)
        }
        ClickButtonTask(controller, logItem, DofusImages.SEARCH_BTN.path).runAndGet()
        return true
    }

    override fun onFailed(event: WorkerStateEvent, logItem: LogItem) {
        controller.closeLog("Quest KO - ${event.source.exception.localizedMessage}", logItem)
    }

    override fun onSucceeded(event: WorkerStateEvent, value: Boolean, logItem: LogItem) {
        controller.closeLog("Quest OK", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Executing quest ...", parentLogItem)
    }

}