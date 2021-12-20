package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.*
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.SetCharacterRestrictionsMessage
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

    fun processMove(gameInfo: GameInfo, clickLocation: PointRelative): Boolean {
        gameInfo.eventStore.clear()
        MouseUtil.leftClick(gameInfo, clickLocation)
        WaitUtil.waitUntilMultipleMessagesArrive(
            gameInfo,
            CurrentMapMessage::class.java,
            MapComplementaryInformationsDataMessage::class.java,
            SetCharacterRestrictionsMessage::class.java
        )
        waitForMapChange(gameInfo)
        return true
    }

    fun isMapChanged(
        gameInfo: GameInfo,
        complementaryInformationClass: Class<out MapComplementaryInformationsDataMessage> = MapComplementaryInformationsDataMessage::class.java
    ): Boolean {
        return gameInfo.eventStore.getAllEvents(SetCharacterRestrictionsMessage::class.java).isNotEmpty()
                && gameInfo.eventStore.getAllEvents(CurrentMapMessage::class.java).isNotEmpty()
                && gameInfo.eventStore.getAllEvents(complementaryInformationClass).isNotEmpty()
    }

    fun waitForMapChange(
        gameInfo: GameInfo,
        complementaryInformationClass: Class<out MapComplementaryInformationsDataMessage> = MapComplementaryInformationsDataMessage::class.java
    ) {
        WaitUtil.waitForEvents(
            gameInfo,
            complementaryInformationClass,
            CurrentMapMessage::class.java,
            SetCharacterRestrictionsMessage::class.java,
        )
        gameInfo.eventStore.clearUntilLast(CurrentMapMessage::class.java)
        WaitUtil.waitForEvents(gameInfo, SetCharacterRestrictionsMessage::class.java)
        gameInfo.eventStore.clearUntilFirst(SetCharacterRestrictionsMessage::class.java)
        WaitUtil.waitForEvent(gameInfo, BasicNoOperationMessage::class.java)
    }

}