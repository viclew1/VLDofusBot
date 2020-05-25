package fr.lewon.dofus.bot.ui.logic.tasks.complex

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.ui.logic.DofusBotTask
import fr.lewon.dofus.bot.util.DofusImages
import fr.lewon.dofus.bot.util.GameInfoUtil
import javafx.concurrent.WorkerStateEvent

class ExecuteHuntTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?,
    private val hintsIdByName: MutableMap<String, List<String>>,
    private val altWorld: Boolean = false
) : DofusBotTask<Boolean>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Boolean {
        while (true) {
            if (GameInfoUtil.getButtonCenter(controller.captureGameImage(), DofusImages.FIGHT_BTN.path) != null)
                return true
            if (!ExecuteQuestTask(controller, logItem, hintsIdByName, altWorld).runAndGet())
                return false
            Thread.sleep(1000)
        }
    }

    override fun onFailed(event: WorkerStateEvent, logItem: LogItem) {
        controller.closeLog("Treasure hunt failed", logItem)
    }

    override fun onSucceeded(event: WorkerStateEvent, value: Boolean, logItem: LogItem) {
        controller.closeLog("Treasure hunt succeeded !", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Executing treasure hunt ...", parentLogItem)
    }
}