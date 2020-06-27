package fr.lewon.dofus.bot.scripts.tasks.impl.complex

import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.RetrieveLocationTask
import fr.lewon.dofus.bot.scripts.tasks.impl.RetrieveNextDirTask
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.DTBRequestProcessor

class ExecuteQuestStepTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?,
    private val alreadyFoundPos: List<Pair<Int, Int>>,
    private val toFindIds: List<String>,
    private val altWorld: Boolean = false
) : DofusBotTask<Pair<Int, Int>>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Pair<Int, Int> {
        val nextDir = RetrieveNextDirTask(controller, logItem)
            .run()
        if (toFindIds.contains("PHO")) {
            return MultimapPhorrorSearchTask(
                controller,
                logItem,
                nextDir,
                alreadyFoundPos
            ).run()
        }
        val location = RetrieveLocationTask(
            controller,
            logItem
        ).run()
        val hint = DTBRequestProcessor.getHint(
            x = location.first,
            y = location.second,
            direction = nextDir,
            toFindId = toFindIds,
            world = if (altWorld) "2" else "0"
        )
        if (hint == null) {
            controller.log("Couldn't retrieve object distance, trying every map.", logItem)
            return MultimapRandomSearchTask(
                controller,
                logItem,
                nextDir
            ).run()
        }
        return MultimapMoveTask(
            controller,
            logItem,
            nextDir,
            hint.d
        ).run()
    }

    override fun onFailed(exception: Exception, logItem: LogItem) {
        controller.closeLog("KO - ${exception.localizedMessage}", logItem)
    }

    override fun onSucceeded(value: Pair<Int, Int>, logItem: LogItem) {
        controller.closeLog("OK", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Executing quest step ... ", parentLogItem)
    }
}