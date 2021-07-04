package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.sniffer.model.messages.GameMapMovementMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object MoveStartEventHandler : EventHandler<GameMapMovementMessage> {

    override fun onEventReceived(socketResult: GameMapMovementMessage) {
        println("move start")
    }

}