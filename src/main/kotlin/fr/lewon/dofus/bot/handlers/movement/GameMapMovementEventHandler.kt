package fr.lewon.dofus.bot.handlers.movement

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameMapMovementMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameMapMovementEventHandler : IEventHandler<GameMapMovementMessage> {

    override fun onEventReceived(socketResult: GameMapMovementMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val toCellId = socketResult.keyMovements.last()
        val fighter = gameInfo.fightBoard.getFighterById(socketResult.actorId)
        if (fighter != null) {
            gameInfo.fightBoard.move(fighter, toCellId)
        } else {
            gameInfo.entityPositionsOnMapByEntityId[socketResult.actorId] = toCellId
        }
    }

}