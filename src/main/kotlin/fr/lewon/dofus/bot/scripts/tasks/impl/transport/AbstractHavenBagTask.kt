package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.chat.TextInformationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.misc.GameContextRefreshEntityLookMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataInHavenBagMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

abstract class AbstractHavenBagTask(private var shouldReachHavenBag: Boolean) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))

        val snifferId = gameInfo.snifferId
        EventStore.clear(snifferId)
        KeyboardUtil.sendKey(gameInfo, 'H', 0)
        if (!WaitUtil.waitUntil({ isToggleHavenBagFinished(snifferId) }, cancellationToken)) {
            error("No message arrived in time. Events in store : ${EventStore.getStoredEventsStr(snifferId)}")
        }
        if (isToggleHavenBagFail(snifferId)) {
            VldbLogger.info("Can't reach haven bag on this map.", logItem)
            return false
        }

        val reachedHavenBag = isReachHavenBagSuccess(snifferId)

        gameInfo.playerId = WaitUtil.waitForEvent(
            snifferId, GameContextRefreshEntityLookMessage::class.java, cancellationToken, false
        ).id
        VldbLogger.info("Player ID is : ${gameInfo.playerId}", logItem)

        MoveUtil.waitForMapChange(gameInfo, cancellationToken, getComplementaryInformationClass(reachedHavenBag))

        if (shouldReachHavenBag && !reachedHavenBag) {
            VldbLogger.info("Left haven bag instead, trying again...", logItem)
            return doExecute(logItem, gameInfo, cancellationToken)
        }
        if (!shouldReachHavenBag && reachedHavenBag) {
            VldbLogger.info("Reached haven bag instead, trying again...", logItem)
            return doExecute(logItem, gameInfo, cancellationToken)
        }
        return true
    }

    private fun getComplementaryInformationClass(inHavenBag: Boolean): Class<out MapComplementaryInformationsDataMessage> {
        return if (inHavenBag) {
            MapComplementaryInformationsDataInHavenBagMessage::class.java
        } else {
            MapComplementaryInformationsDataMessage::class.java
        }
    }

    private fun isToggleHavenBagFinished(snifferId: Long): Boolean {
        return isToggleHavenBagFail(snifferId) || isReachHavenBagSuccess(snifferId) || isLeaveHavenBagSuccess(snifferId)
    }

    private fun isToggleHavenBagFail(snifferId: Long): Boolean {
        val lastTextInformation = EventStore.getLastEvent(TextInformationMessage::class.java, snifferId)
        return lastTextInformation != null && lastTextInformation.msgId == 471
    }

    private fun isReachHavenBagSuccess(snifferId: Long): Boolean {
        return EventStore.getFirstEvent(
            MapComplementaryInformationsDataInHavenBagMessage::class.java,
            snifferId
        ) != null && MoveUtil.isMapChanged(snifferId, getComplementaryInformationClass(true))
    }

    private fun isLeaveHavenBagSuccess(snifferId: Long): Boolean {
        return EventStore.getFirstEvent(
            MapComplementaryInformationsDataMessage::class.java,
            snifferId
        ) != null && MoveUtil.isMapChanged(snifferId, getComplementaryInformationClass(false))
    }

}