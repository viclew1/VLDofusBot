package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.*
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil

object MoveUtil {

    fun buildMoveTask(direction: Direction): MoveTask {
        return when (direction) {
            Direction.LEFT -> MoveLeftTask()
            Direction.RIGHT -> MoveRightTask()
            Direction.TOP -> MoveTopTask()
            Direction.BOTTOM -> MoveBottomTask()
        }
    }

    fun processMove(clickLocation: PointAbsolute): Boolean {
        return processMove(ConverterUtil.toPointRelative(clickLocation))
    }

    fun processMove(clickLocation: PointRelative): Boolean {
        MouseUtil.leftClick(clickLocation, false, 0)
        EventStore.clear(BasicNoOperationMessage::class.java)
        val succeeded = WaitUtil.waitForEventWithoutError(MapComplementaryInformationsDataMessage::class.java) != null
        if (!succeeded) {
            return false
        }
        WaitUtil.waitForEventWithoutError(BasicNoOperationMessage::class.java)
        return true
    }

}