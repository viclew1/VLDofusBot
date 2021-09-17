package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightTeleportOnSameMapMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

object GameActionFightTeleportOnSameMapEventHandler : EventHandler<GameActionFightTeleportOnSameMapMessage> {

    override fun onEventReceived(socketResult: GameActionFightTeleportOnSameMapMessage) {
        val fighterId = socketResult.targetId
        val cellId = socketResult.cellId
        GameInfo.fightBoard.move(fighterId, cellId)
        ConsoleLogger.info("Fighter [$fighterId] teleported to cell [$cellId]")
    }

}