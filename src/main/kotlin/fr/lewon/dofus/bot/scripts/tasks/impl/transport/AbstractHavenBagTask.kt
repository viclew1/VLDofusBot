package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.chat.TextInformationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.misc.GameContextRefreshEntityLookMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataInHavenBagMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import java.awt.event.KeyEvent

abstract class AbstractHavenBagTask(private var reachHavenBag: Boolean) : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem): Boolean {
        MouseUtil.leftClick(ConfigManager.config.mouseRestPos)

        EventStore.clear(TextInformationMessage::class.java)
        EventStore.clear(GameContextRefreshEntityLookMessage::class.java)
        EventStore.clear(MapComplementaryInformationsDataInHavenBagMessage::class.java)
        EventStore.clear(MapComplementaryInformationsDataMessage::class.java)

        KeyboardUtil.sendKey(KeyEvent.VK_H, 0)
        WaitUtil.waitUntil({ isToggleHavenBagFail() || isReachHavenBagSuccess() || isLeaveHavenBagSuccess() })
        if (isToggleHavenBagFail()) {
            return false
        }
        WaitUtil.waitForEvent(BasicNoOperationMessage::class.java)
        if (reachHavenBag && isLeaveHavenBagSuccess()) {
            VldbLogger.info("Left haven bag instead, trying again...", logItem)
            return execute(logItem)
        }
        if (!reachHavenBag && isReachHavenBagSuccess()) {
            VldbLogger.info("Reached haven bag instead, trying again...", logItem)
            return execute(logItem)
        }
        WaitUtil.waitUntil({ EventStore.getAllEvents(GameContextRefreshEntityLookMessage::class.java).isNotEmpty() })
        val refreshEntityMessage = EventStore.getAllEvents(GameContextRefreshEntityLookMessage::class.java).first()
        GameInfo.playerId = refreshEntityMessage.id
        VldbLogger.info("Player ID is : ${GameInfo.playerId}", logItem)
        return true
    }

    private fun isToggleHavenBagFail(): Boolean {
        val lastTextInformation = EventStore.getLastEvent(TextInformationMessage::class.java)
        return lastTextInformation != null && lastTextInformation.msgId == 471
    }

    private fun isReachHavenBagSuccess(): Boolean {
        return EventStore.getLastEvent(MapComplementaryInformationsDataInHavenBagMessage::class.java) != null
    }

    private fun isLeaveHavenBagSuccess(): Boolean {
        return EventStore.getLastEvent(MapComplementaryInformationsDataMessage::class.java) != null
    }
}