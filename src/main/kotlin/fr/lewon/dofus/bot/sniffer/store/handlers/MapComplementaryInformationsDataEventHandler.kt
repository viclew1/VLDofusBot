package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

object MapComplementaryInformationsDataEventHandler : EventHandler<MapComplementaryInformationsDataMessage> {

    override fun onEventReceived(socketResult: MapComplementaryInformationsDataMessage) {
        GameInfo.currentMap = socketResult.dofusMap
        GameInfo.phorrorOnMap = socketResult.isPhorrorHere()
        GameInfo.inHavenBag = false
        GameInfo.fightBoard.updateStartCells(socketResult.fightStartPositions.positionsForChallenger)
        GameInfo.fightBoard.resetFighters()
        ConsoleLogger.info("[${socketResult.dofusMap.posX},${socketResult.dofusMap.posY}] ; World : ${socketResult.dofusMap.worldMap} ; Phorror here : ${socketResult.isPhorrorHere()}")
    }

}