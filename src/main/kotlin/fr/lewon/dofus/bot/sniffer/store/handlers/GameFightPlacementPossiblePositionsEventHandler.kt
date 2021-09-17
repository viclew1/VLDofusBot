package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightPlacementPossiblePositionsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

object GameFightPlacementPossiblePositionsEventHandler : EventHandler<GameFightPlacementPossiblePositionsMessage> {

    override fun onEventReceived(socketResult: GameFightPlacementPossiblePositionsMessage) {
        GameInfo.fightBoard.updateStartCells(socketResult.positionsForChallengers)
        ConsoleLogger.info("Fight start cells updated")
    }

}