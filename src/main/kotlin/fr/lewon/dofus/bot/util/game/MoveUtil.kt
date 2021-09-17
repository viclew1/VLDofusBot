package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.MouseUtil

object MoveUtil {

    fun processMove(clickLocation: PointAbsolute): MapComplementaryInformationsDataMessage {
        return processMove(ConverterUtil.toPointRelative(clickLocation))
    }

    fun processMove(clickLocation: PointRelative): MapComplementaryInformationsDataMessage {
        MouseUtil.leftClick(clickLocation, false, 0)
        val mapInfo = EventStore.waitForEvent(MapComplementaryInformationsDataMessage::class.java)
        EventStore.waitForEvent(BasicNoOperationMessage::class.java)
        return mapInfo
    }

}