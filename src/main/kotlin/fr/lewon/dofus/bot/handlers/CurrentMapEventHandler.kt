package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.move.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.manager.d2p.FightCellManager

object CurrentMapEventHandler : EventHandler<CurrentMapMessage> {
    override fun onEventReceived(socketResult: CurrentMapMessage) {
        val cellDataList = FightCellManager.getCellDataList(socketResult.map.id, socketResult.mapKey)
        GameInfo.fightBoard.updateCells(cellDataList)
    }
}