package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.sniffer.model.messages.move.GameMapMovementMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameMapMovementEventHandler : EventHandler<GameMapMovementMessage> {

    override fun onEventReceived(socketResult: GameMapMovementMessage, snifferId: Long) {
        val gameInfo = GameSnifferUtil.getGameInfoBySnifferId(snifferId)
        val fromCellId = socketResult.keyMovements.first()
        val toCellId = socketResult.keyMovements.last()
        val fighter = gameInfo.fightBoard.getFighterById(socketResult.actorId)
        if (fighter != null) {
            gameInfo.fightBoard.move(fighter, toCellId)
            VldbLogger.debug("Fighter [${socketResult.actorId}] moved from cell [$fromCellId] to cell [$toCellId]")
        } else if (gameInfo.playerId == socketResult.actorId) {
            gameInfo.entityPositionsOnMapByEntityId[socketResult.actorId] = toCellId
            VldbLogger.debug("Entity [${socketResult.actorId}] moved from cell [$fromCellId] to cell [$toCellId]")
        }
    }

}