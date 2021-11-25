package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightPlacementPossiblePositionsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameFightPlacementPossiblePositionsEventHandler : EventHandler<GameFightPlacementPossiblePositionsMessage> {

    override fun onEventReceived(socketResult: GameFightPlacementPossiblePositionsMessage, snifferId: Long) {
        val gameInfo = GameSnifferUtil.getGameInfoBySnifferId(snifferId)
        gameInfo.dofusBoard.updateStartCells(socketResult.positionsForChallengers)
        VldbLogger.debug("Fight start cells updated")
    }

}