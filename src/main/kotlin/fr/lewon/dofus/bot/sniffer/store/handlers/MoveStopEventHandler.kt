package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.sniffer.model.messages.GameMapMovementConfirmMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object MoveStopEventHandler : EventHandler<GameMapMovementConfirmMessage> {

    override fun onEventReceived(socketResult: GameMapMovementConfirmMessage) {
        println("move stop")
    }

}