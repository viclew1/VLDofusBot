package fr.lewon.dofus.bot.handlers.fight

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.GameFightPlacementPossiblePositionsMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameFightPlacementPossiblePositionsEventHandler : IEventHandler<GameFightPlacementPossiblePositionsMessage> {

    override fun onEventReceived(
        socketResult: GameFightPlacementPossiblePositionsMessage,
        connection: DofusConnection
    ) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.dofusBoard.updateStartCells(socketResult.positionsForChallengers)
    }

}