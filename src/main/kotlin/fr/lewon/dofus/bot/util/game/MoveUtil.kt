package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.*
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

object MoveUtil {

    fun buildMoveTask(direction: Direction, linkedZoneCellId: Int? = null): MoveTask {
        return when (direction) {
            Direction.LEFT -> MoveLeftTask(linkedZoneCellId)
            Direction.RIGHT -> MoveRightTask(linkedZoneCellId)
            Direction.TOP -> MoveTopTask(linkedZoneCellId)
            Direction.BOTTOM -> MoveBottomTask(linkedZoneCellId)
        }
    }

    fun processMove(gameInfo: GameInfo, clickLocation: PointRelative, cancellationToken: CancellationToken): Boolean {
        val snifferId = gameInfo.snifferId
        EventStore.clear(snifferId)
        RetryUtil.tryUntilSuccess(
            { MouseUtil.leftClick(gameInfo, clickLocation) },
            { WaitUtil.waitForEvent(snifferId, BasicNoOperationMessage::class.java, cancellationToken, 3000) != null },
            3
        )
        return waitForMapChange(gameInfo, cancellationToken, false)
    }

    fun waitForMapChange(
        gameInfo: GameInfo,
        cancellationToken: CancellationToken,
        clearEventStore: Boolean = true
    ): Boolean {
        return waitForCurrentMapSequence(gameInfo, cancellationToken, clearEventStore)
                && waitForComplementaryMapInformationSequence(gameInfo, cancellationToken)
    }

    private fun waitForCurrentMapSequence(
        gameInfo: GameInfo,
        cancellationToken: CancellationToken,
        clearEventStore: Boolean = true
    ): Boolean {
        return WaitUtil.waitForSequence(
            gameInfo.snifferId,
            CurrentMapMessage::class.java,
            BasicNoOperationMessage::class.java,
            clearEventStore = clearEventStore,
            cancellationToken = cancellationToken
        )
    }

    private fun waitForComplementaryMapInformationSequence(
        gameInfo: GameInfo,
        cancellationToken: CancellationToken,
    ): Boolean {
        return WaitUtil.waitForSequence(
            gameInfo.snifferId,
            MapComplementaryInformationsDataMessage::class.java,
            BasicNoOperationMessage::class.java,
            clearEventStore = false,
            cancellationToken = cancellationToken
        )
    }

}