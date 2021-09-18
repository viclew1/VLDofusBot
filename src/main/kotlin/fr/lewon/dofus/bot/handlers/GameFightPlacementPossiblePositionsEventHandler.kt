package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightPlacementPossiblePositionsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.logs.VldbLogger

object GameFightPlacementPossiblePositionsEventHandler : EventHandler<GameFightPlacementPossiblePositionsMessage> {

    override fun onEventReceived(socketResult: GameFightPlacementPossiblePositionsMessage) {
        GameInfo.fightBoard.updateStartCells(socketResult.positionsForChallengers)
        VldbLogger.info("Fight start cells updated")
    }

}