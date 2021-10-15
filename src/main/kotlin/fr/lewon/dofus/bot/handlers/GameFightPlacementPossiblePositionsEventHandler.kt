package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightPlacementPossiblePositionsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object GameFightPlacementPossiblePositionsEventHandler : EventHandler<GameFightPlacementPossiblePositionsMessage> {

    override fun onEventReceived(socketResult: GameFightPlacementPossiblePositionsMessage) {
        GameInfo.fightBoard.updateStartCells(socketResult.positionsForChallengers)
        VldbLogger.debug("Fight start cells updated")
    }

}