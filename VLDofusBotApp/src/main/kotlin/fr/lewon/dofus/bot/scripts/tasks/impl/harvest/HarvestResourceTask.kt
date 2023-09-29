package fr.lewon.dofus.bot.scripts.tasks.impl.harvest

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.exceptions.DofusBotTaskFatalException
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightTask
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameContextDestroyMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameMapMovementConfirmMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.job.JobLevelUpMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.interactive.InteractiveUseErrorMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.interactive.InteractiveUseRequestMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items.ObjectAddedMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items.ObjectQuantityMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil

class HarvestResourceTask(private val interactiveElement: InteractiveElement) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        gameInfo.eventStore.clear()
        val sendClickLogItem = gameInfo.logger.addSubLog("Clicking on harvestable ...", logItem)
        try {
            InteractiveUtil.useInteractive(
                gameInfo = gameInfo,
                elementId = interactiveElement.elementId,
                skillId = interactiveElement.enabledSkills.first().skillId
            )
        } catch (e: Exception) {
            closeInteractiveUseFailurePopup(gameInfo)
            gameInfo.logger.closeLog("KO", sendClickLogItem)
            return false
        }
        gameInfo.logger.closeLog("OK", sendClickLogItem)

        val reachHarvestableLogItem = gameInfo.logger.addSubLog("Reaching harvestable ...", logItem)
        if (!WaitUtil.waitUntil(8000) {
                gameInfo.eventStore.getLastEvent(InteractiveUseRequestMessage::class.java) != null
                    || gameInfo.eventStore.getLastEvent(GameMapMovementConfirmMessage::class.java) != null
            }) {
            gameInfo.logger.closeLog("KO", reachHarvestableLogItem)
            return false
        }
        gameInfo.logger.closeLog("OK", reachHarvestableLogItem)

        val useHarvestableLogItem = gameInfo.logger.addSubLog("Interacting with harvestable ...", logItem)
        if (!WaitUtil.waitUntil(1000) {
                gameInfo.eventStore.getLastEvent(InteractiveUseRequestMessage::class.java) != null
            }) {
            gameInfo.logger.closeLog("KO", useHarvestableLogItem)
            return false
        }
        gameInfo.logger.closeLog("OK", useHarvestableLogItem)

        val waitingForHarvestResultLogItem = gameInfo.logger.addSubLog("Waiting until harvest is done ...", logItem)
        if (!WaitUtil.waitUntil {
                gameInfo.eventStore.getLastEvent(ObjectQuantityMessage::class.java) != null ||
                    gameInfo.eventStore.getLastEvent(ObjectAddedMessage::class.java) != null ||
                    gameInfo.eventStore.getLastEvent(GameContextDestroyMessage::class.java) != null ||
                    gameInfo.eventStore.getLastEvent(InteractiveUseErrorMessage::class.java) != null
            }) {
            gameInfo.logger.closeLog("KO", waitingForHarvestResultLogItem)
            throw DofusBotTaskFatalException("Unexpected error, harvest has started but never finished.")
        }
        gameInfo.logger.closeLog("OK", waitingForHarvestResultLogItem)

        if (gameInfo.eventStore.getLastEvent(InteractiveUseErrorMessage::class.java) != null) {
            WaitUtil.sleep(500)
            return false
        }

        WaitUtil.sleep(100)
        val fightStarted = gameInfo.eventStore.getLastEvent(GameContextDestroyMessage::class.java) != null

        if (gameInfo.eventStore.getLastEvent(JobLevelUpMessage::class.java) != null) {
            closeJobLevelUpPopup(gameInfo, fightStarted)
        }

        if (fightStarted) {
            if (!FightTask().run(logItem, gameInfo)) {
                throw DofusBotTaskFatalException("Fight has failed.")
            }
        }
        return gameInfo.eventStore.getLastEvent(ObjectQuantityMessage::class.java) != null ||
            gameInfo.eventStore.getLastEvent(ObjectAddedMessage::class.java) != null
    }

    private fun closeInteractiveUseFailurePopup(gameInfo: GameInfo) {
        if (WaitUtil.waitUntil(500) { isPopupOpened(gameInfo, false) }) {
            closeJobLevelUpPopup(gameInfo, false)
        }
    }

    private fun closeJobLevelUpPopup(gameInfo: GameInfo, fightStarted: Boolean) {
        val popupOpened = WaitUtil.waitUntil(10000) { isPopupOpened(gameInfo, fightStarted) }
        if (!popupOpened) {
            error("Couldn't find job level up popup")
        }
        UiUtil.closeWindow(gameInfo, DofusUIElement.TIPS, fightStarted)
        val uiElementClosed = WaitUtil.waitUntil(10000) { !isPopupOpened(gameInfo, fightStarted) }
        if (!uiElementClosed) {
            error("Couldn't close job level up popup")
        }
    }

    private fun isPopupOpened(gameInfo: GameInfo, fightStarted: Boolean): Boolean =
        UiUtil.isUiElementWindowOpened(
            gameInfo,
            DofusUIElement.TIPS,
            fightStarted,
            closeButtonBackgroundColorMin = DofusColors.HIGHLIGHT_COLOR_MIN,
            closeButtonBackgroundColorMax = DofusColors.HIGHLIGHT_COLOR_MAX
        )

    override fun onStarted(): String {
        return "Harvesting resource ..."
    }
}