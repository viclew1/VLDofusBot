package fr.lewon.dofus.bot.scripts.tasks.impl.init

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.quest.QuestListMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.quest.QuestStepInfoMessage
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil

class UpdateQuestsTask : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))
        gameInfo.eventStore.clear()
        KeyboardUtil.sendKey(gameInfo, 'Q', 0)
        WaitUtil.waitForEvents(gameInfo, QuestListMessage::class.java, QuestStepInfoMessage::class.java)
        if (!WaitUtil.waitUntil { UiUtil.isUiElementWindowOpened(gameInfo, DofusUIElement.QUEST_BASE) }) {
            error("Couldn't open tab window. Is Q your hotkey to open it ?")
        }
        val closeButtonBounds = UiUtil.getContainerBounds(DofusUIElement.QUEST_BASE, "btn_close")
        MouseUtil.leftClick(gameInfo, closeButtonBounds.getCenter())
        if (!WaitUtil.waitUntil { !UiUtil.isUiElementWindowOpened(gameInfo, DofusUIElement.QUEST_BASE) }) {
            error("Couldn't close quests")
        }
        return true
    }

    override fun onStarted(): String {
        return "Initializing quests information ..."
    }
}