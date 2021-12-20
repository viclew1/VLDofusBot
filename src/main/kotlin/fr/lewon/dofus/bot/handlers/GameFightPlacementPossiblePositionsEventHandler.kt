package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightPlacementPossiblePositionsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameFightPlacementPossiblePositionsEventHandler : EventHandler<GameFightPlacementPossiblePositionsMessage> {

    override fun onEventReceived(
        socketResult: GameFightPlacementPossiblePositionsMessage,
        connection: DofusConnection
    ) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.dofusBoard.updateStartCells(socketResult.positionsForChallengers)
        gameInfo.logger.debug("Fight start cells updated")
    }

}