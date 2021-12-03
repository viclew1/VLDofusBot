package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.*
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.GameMapMovementMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.SetCharacterRestrictionsMessage
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

    fun processMove(
        gameInfo: GameInfo,
        clickLocation: PointRelative,
        cancellationToken: CancellationToken
    ): Boolean {
        val snifferId = gameInfo.snifferId
        val isMapChangeAttemptedFunc = { isMapChangeRequested(gameInfo) || isMapChanged(snifferId) }

        EventStore.clear(snifferId)
        MouseUtil.leftClick(gameInfo, clickLocation)
        if (!WaitUtil.waitUntil(isMapChangeAttemptedFunc, cancellationToken)) {
            error("Map request change failed. Event store content : \n${EventStore.getStoredEventsStr(snifferId)}")
        }
        waitForMapChange(gameInfo, cancellationToken)
        return true
    }

    private fun isMapChangeRequested(gameInfo: GameInfo): Boolean {
        val snifferId = gameInfo.snifferId
        return EventStore.getAllEvents(BasicNoOperationMessage::class.java, snifferId).isNotEmpty()
                && EventStore.getLastEvent(GameMapMovementMessage::class.java, snifferId)?.actorId == gameInfo.playerId
    }

    fun isMapChanged(
        snifferId: Long,
        complementaryInformationClass: Class<out MapComplementaryInformationsDataMessage> = MapComplementaryInformationsDataMessage::class.java
    ): Boolean {
        return EventStore.getAllEvents(SetCharacterRestrictionsMessage::class.java, snifferId).size >= 2
                && EventStore.getAllEvents(CurrentMapMessage::class.java, snifferId).isNotEmpty()
                && EventStore.getAllEvents(complementaryInformationClass, snifferId).isNotEmpty()
    }

    fun waitForMapChange(
        gameInfo: GameInfo,
        cancellationToken: CancellationToken,
        complementaryInformationClass: Class<out MapComplementaryInformationsDataMessage> = MapComplementaryInformationsDataMessage::class.java
    ) {
        WaitUtil.waitForEvents(
            gameInfo.snifferId,
            complementaryInformationClass,
            CurrentMapMessage::class.java,
            SetCharacterRestrictionsMessage::class.java,
            SetCharacterRestrictionsMessage::class.java,
            cancellationToken = cancellationToken,
            removeWhenFound = false
        )
        EventStore.clearUntilLast(gameInfo.snifferId, SetCharacterRestrictionsMessage::class.java)
        WaitUtil.waitForEvent(gameInfo.snifferId, BasicNoOperationMessage::class.java, cancellationToken)
    }

}