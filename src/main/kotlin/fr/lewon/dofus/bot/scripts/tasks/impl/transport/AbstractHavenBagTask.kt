package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.chat.TextInformationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.misc.GameContextRefreshEntityLookMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataInHavenBagMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.event.KeyEvent

abstract class AbstractHavenBagTask(private var reachHavenBag: Boolean) : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo), 200)

        EventStore.clear(BasicNoOperationMessage::class.java, gameInfo.snifferId)
        EventStore.clear(TextInformationMessage::class.java, gameInfo.snifferId)
        EventStore.clear(GameContextRefreshEntityLookMessage::class.java, gameInfo.snifferId)
        EventStore.clear(MapComplementaryInformationsDataInHavenBagMessage::class.java, gameInfo.snifferId)
        EventStore.clear(MapComplementaryInformationsDataMessage::class.java, gameInfo.snifferId)

        val snifferId = gameInfo.snifferId
        KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_H, 0)
        if (!WaitUtil.waitUntil({ isToggleHavenBagFinished(snifferId) }, cancellationToken)) {
            return false
        }
        if (isToggleHavenBagFail(snifferId)) {
            return false
        }
        if (reachHavenBag && isLeaveHavenBagSuccess(snifferId)) {
            VldbLogger.info("Left haven bag instead, trying again...", logItem)
            return execute(logItem, gameInfo, cancellationToken)
        }
        if (!reachHavenBag && isReachHavenBagSuccess(snifferId)) {
            VldbLogger.info("Reached haven bag instead, trying again...", logItem)
            return execute(logItem, gameInfo, cancellationToken)
        }
        val refreshEntityLookMessageReceived = WaitUtil.waitUntil(
            { EventStore.getAllEvents(GameContextRefreshEntityLookMessage::class.java, snifferId).isNotEmpty() },
            cancellationToken
        )
        if (!refreshEntityLookMessageReceived) {
            return false
        }
        val refreshEntityMessage =
            EventStore.getAllEvents(GameContextRefreshEntityLookMessage::class.java, snifferId).first()
        gameInfo.playerId = refreshEntityMessage.id
        VldbLogger.info("Player ID is : ${gameInfo.playerId}", logItem)
        return true
    }

    private fun isToggleHavenBagFinished(snifferId: Long): Boolean {
        return isToggleHavenBagFail(snifferId) || isReachHavenBagSuccess(snifferId) || isLeaveHavenBagSuccess(snifferId)
    }

    private fun isToggleHavenBagFail(snifferId: Long): Boolean {
        val lastTextInformation = EventStore.getLastEvent(TextInformationMessage::class.java, snifferId)
        return lastTextInformation != null && lastTextInformation.msgId == 471
    }

    private fun isReachHavenBagSuccess(snifferId: Long): Boolean {
        return EventStore.getLastEvent(MapComplementaryInformationsDataInHavenBagMessage::class.java, snifferId) != null
                && EventStore.getAllEvents(BasicNoOperationMessage::class.java, snifferId).size > 1
    }

    private fun isLeaveHavenBagSuccess(snifferId: Long): Boolean {
        return EventStore.getLastEvent(MapComplementaryInformationsDataMessage::class.java, snifferId) != null
                && EventStore.getAllEvents(BasicNoOperationMessage::class.java, snifferId).size > 1
    }
}