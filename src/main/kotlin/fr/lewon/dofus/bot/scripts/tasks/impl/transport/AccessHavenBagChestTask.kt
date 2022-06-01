package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.storage.StorageInventoryContentMessage
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil

class AccessHavenBagChestTask : BooleanDofusBotTask() {

    companion object {
        private const val CHEST_ELEMENT_ID = 502552
        private const val SKILL_ID = 184
    }

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (!ReachHavenBagTask().run(logItem, gameInfo)) {
            error("Couldn't reach haven bag")
        }
        gameInfo.eventStore.clear()
        InteractiveUtil.useInteractive(gameInfo, CHEST_ELEMENT_ID, SKILL_ID)
        WaitUtil.waitUntilMessageArrives(gameInfo, StorageInventoryContentMessage::class.java)
        if (!WaitUtil.waitUntil({ UiUtil.isWindowOpenedUsingCloseButton(gameInfo, DofusUIElement.STORAGE) })) {
            error("Couldn't open chest storage")
        }
        return true
    }

    override fun onStarted(): String {
        return "Accessing haven bag chest ... "
    }
}