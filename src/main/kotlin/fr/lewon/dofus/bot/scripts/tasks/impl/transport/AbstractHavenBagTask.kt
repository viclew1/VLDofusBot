package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.chat.TextInformationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.misc.GameContextRefreshEntityLookMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataInHavenBagMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.event.KeyEvent

abstract class AbstractHavenBagTask(private var shouldReachHavenBag: Boolean) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))

        val snifferId = gameInfo.snifferId
        EventStore.clear(snifferId)
        KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_H, 0)
        if (!WaitUtil.waitUntil({ isToggleHavenBagFinished(snifferId) }, cancellationToken)) {
            error("No message arrived in time. Events in store : ${EventStore.getStoredEventsStr(snifferId)}")
        }
        if (isToggleHavenBagFail(snifferId)) {
            return false
        }

        val reachedHavenBag = if (isLeaveHavenBagSuccess(snifferId)) {
            clearLeaveHavenBagSequence(snifferId)
            false
        } else if (isReachHavenBagSuccess(snifferId)) {
            clearReachHavenBagSequence(snifferId)
            true
        } else {
            error("Unknown haven bag state")
        }

        WaitUtil.waitForSequence(
            snifferId,
            CurrentMapMessage::class.java,
            BasicNoOperationMessage::class.java,
            false,
            cancellationToken = cancellationToken
        )

        if (shouldReachHavenBag && !reachedHavenBag) {
            VldbLogger.info("Left haven bag instead, trying again...", logItem)
            return doExecute(logItem, gameInfo, cancellationToken)
        }
        if (!shouldReachHavenBag && reachedHavenBag) {
            VldbLogger.info("Reached haven bag instead, trying again...", logItem)
            return doExecute(logItem, gameInfo, cancellationToken)
        }

        gameInfo.playerId =
            WaitUtil.waitForEvent(snifferId, GameContextRefreshEntityLookMessage::class.java, cancellationToken)?.id
                ?: error("No message of type : ${GameContextRefreshEntityLookMessage::class.java.typeName}")
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
        return EventStore.containsSequence(
            snifferId,
            MapComplementaryInformationsDataInHavenBagMessage::class.java,
            BasicNoOperationMessage::class.java
        )
    }

    private fun clearReachHavenBagSequence(snifferId: Long) {
        EventStore.removeSequence(
            snifferId,
            MapComplementaryInformationsDataInHavenBagMessage::class.java,
            BasicNoOperationMessage::class.java
        )
    }

    private fun isLeaveHavenBagSuccess(snifferId: Long): Boolean {
        return EventStore.containsSequence(
            snifferId,
            MapComplementaryInformationsDataMessage::class.java,
            BasicNoOperationMessage::class.java
        )
    }

    private fun clearLeaveHavenBagSequence(snifferId: Long) {
        EventStore.removeSequence(
            snifferId,
            MapComplementaryInformationsDataMessage::class.java,
            BasicNoOperationMessage::class.java
        )
    }

}