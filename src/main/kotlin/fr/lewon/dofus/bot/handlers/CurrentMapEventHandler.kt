package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.move.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.core.manager.MapCellDataUtil

object CurrentMapEventHandler : EventHandler<CurrentMapMessage> {
    override fun onEventReceived(socketResult: CurrentMapMessage) {
        val cellDataList = MapCellDataUtil.getCellDataList(socketResult.map.id, socketResult.mapKey)
        GameInfo.fightBoard.updateCells(cellDataList)
    }
}