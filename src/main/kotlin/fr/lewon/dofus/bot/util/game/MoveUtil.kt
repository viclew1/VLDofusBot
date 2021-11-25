package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.*
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
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

    fun processMove(gameInfo: GameInfo, clickLocation: PointAbsolute, cancellationToken: CancellationToken): Boolean {
        return processMove(gameInfo, ConverterUtil.toPointRelative(gameInfo, clickLocation), cancellationToken)
    }

    fun processMove(gameInfo: GameInfo, clickLocation: PointRelative, cancellationToken: CancellationToken): Boolean {
        MouseUtil.leftClick(gameInfo, clickLocation)
        try {
            WaitUtil.waitForEvent(
                gameInfo.snifferId,
                CurrentMapMessage::class.java,
                cancellationToken = cancellationToken
            )
        } catch (e: Exception) {
            return false
        }
        EventStore.clear(MapComplementaryInformationsDataMessage::class.java, gameInfo.snifferId)
        EventStore.clear(BasicNoOperationMessage::class.java, gameInfo.snifferId)
        return WaitUtil.waitUntil({ isMoveFullyDone(gameInfo) }, cancellationToken)
    }

    private fun isMoveFullyDone(gameInfo: GameInfo): Boolean {
        return EventStore.getLastEvent(MapComplementaryInformationsDataMessage::class.java, gameInfo.snifferId) != null
                && EventStore.getLastEvent(BasicNoOperationMessage::class.java, gameInfo.snifferId) != null
    }

}