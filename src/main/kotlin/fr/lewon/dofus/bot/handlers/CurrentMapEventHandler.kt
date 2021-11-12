package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.manager.d2p.maps.D2PMapsAdapter
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.move.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object CurrentMapEventHandler : EventHandler<CurrentMapMessage> {
    override fun onEventReceived(socketResult: CurrentMapMessage) {
        val cellDataList = D2PMapsAdapter.getCellDataList(socketResult.map.id, socketResult.mapKey)
        GameInfo.fightBoard.updateCells(cellDataList)
    }
}