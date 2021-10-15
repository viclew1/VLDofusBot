package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.hunt.GameRolePlayTreasureHintInformations
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object MapComplementaryInformationsDataEventHandler : EventHandler<MapComplementaryInformationsDataMessage> {

    override fun onEventReceived(socketResult: MapComplementaryInformationsDataMessage) {
        GameInfo.currentMap = socketResult.map
        GameInfo.inHavenBag = false
        GameInfo.phorrorOnMap = socketResult.actors.firstOrNull { it is GameRolePlayTreasureHintInformations } != null
        GameInfo.fightBoard.updateStartCells(socketResult.fightStartPositions.positionsForChallenger)
        GameInfo.fightBoard.resetFighters()
        VldbLogger.debug("Moved to map [${socketResult.map.posX},${socketResult.map.posY}] ; World : ${socketResult.map.worldMap}")
    }

}