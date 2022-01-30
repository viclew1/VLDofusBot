package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.move.GameMapMovementMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameMapMovementEventHandler : EventHandler<GameMapMovementMessage> {

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