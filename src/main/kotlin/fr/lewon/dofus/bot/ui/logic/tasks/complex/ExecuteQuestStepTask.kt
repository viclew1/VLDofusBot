package fr.lewon.dofus.bot.ui.logic.tasks.complex

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.ui.logic.DofusBotTask
import fr.lewon.dofus.bot.ui.logic.tasks.RetrieveLocationTask
import fr.lewon.dofus.bot.ui.logic.tasks.RetrieveNextDirTask
import fr.lewon.dofus.bot.util.DTBRequestProcessor
import javafx.concurrent.WorkerStateEvent

class ExecuteQuestStepTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?,
    private val alreadyFoundPos: List<Pair<Int, Int>>,
    private val toFindIds: List<String>,
    private val altWorld: Boolean = false
) : DofusBotTask<Pair<Int, Int>>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Pair<Int, Int> {
        val nextDir = RetrieveNextDirTask(controller, logItem).runAndGet()
        if (toFindIds.contains("PHO")) {
            return MultimapPhorrorSearchTask(controller, logItem, nextDir, alreadyFoundPos).runAndGet()
        }
        val location = RetrieveLocationTask(controller, logItem).runAndGet()
        val hint = DTBRequestProcessor.getHint(
            x = location.first,
            y = location.second,
            direction = nextDir,
            toFindId = toFindIds,
            world = if (altWorld) "2" else "0"
        )
        if (hint == null) {
            controller.log("Couldn't retrieve object distance, trying every map.", logItem)
            return MultimapRandomSearchTask(controller, logItem, nextDir).runAndGet()
        }
        return MultimapMoveTask(controller, logItem, nextDir, hint.d).runAndGet()
    }

    override fun onFailed(event: WorkerStateEvent, logItem: LogItem) {
        controller.closeLog("KO - ${event.source.exception.localizedMessage}", logItem)
    }

    override fun onSucceeded(event: WorkerStateEvent, value: Pair<Int, Int>, logItem: LogItem) {
        controller.closeLog("OK", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Executing quest step ... ", parentLogItem)
    }
}