package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.move.GameMapMovementMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object GameMapMovementEventHandler : EventHandler<GameMapMovementMessage> {

    override fun onEventReceived(socketResult: GameMapMovementMessage) {
        val fromCellId = socketResult.keyMovements.first()
        val toCellId = socketResult.keyMovements.last()
        val fighter = GameInfo.fightBoard.getFighterById(socketResult.actorId)
        if (fighter != null) {
            GameInfo.fightBoard.move(fighter, toCellId)
            VldbLogger.debug("Fighter [${socketResult.actorId}] moved from cell [$fromCellId] to cell [$toCellId]")
        } else if (GameInfo.playerId == socketResult.actorId) {
            GameInfo.entityPositionsOnMapByEntityId[socketResult.actorId] = toCellId
            VldbLogger.debug("Entity [${socketResult.actorId}] moved from cell [$fromCellId] to cell [$toCellId]")
        }
    }

}