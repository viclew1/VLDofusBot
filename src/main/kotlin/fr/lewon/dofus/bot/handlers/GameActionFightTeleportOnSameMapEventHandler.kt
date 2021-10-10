package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightTeleportOnSameMapMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.logs.VldbLogger

object GameActionFightTeleportOnSameMapEventHandler : EventHandler<GameActionFightTeleportOnSameMapMessage> {

    override fun onEventReceived(socketResult: GameActionFightTeleportOnSameMapMessage) {
        val fighterId = socketResult.targetId
        val cellId = socketResult.cellId
        GameInfo.fightBoard.move(fighterId, cellId)
        VldbLogger.info("Fighter [$fighterId] teleported to cell [$cellId]")
    }

}