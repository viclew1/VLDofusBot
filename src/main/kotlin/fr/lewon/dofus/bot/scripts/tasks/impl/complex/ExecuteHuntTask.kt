package fr.lewon.dofus.bot.scripts.tasks.impl.complex

import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.DofusImages
import fr.lewon.dofus.bot.util.GameInfoUtil

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
            if (!ExecuteQuestTask(
                    controller,
                    logItem,
                    hintsIdByName,
                    altWorld
                ).run()
            )
                return false
            Thread.sleep(1000)
        }
    }

    override fun onFailed(exception: Exception, logItem: LogItem) {
        controller.closeLog("Treasure hunt failed", logItem)
    }

    override fun onSucceeded(value: Boolean, logItem: LogItem) {
        controller.closeLog("Treasure hunt succeeded !", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Executing treasure hunt ...", parentLogItem)
    }
}