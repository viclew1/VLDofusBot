package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.sniffer.model.messages.GameFightTurnReadyMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object TurnReadyEventHandler : EventHandler<GameFightTurnReadyMessage> {

    override fun onEventReceived(socketResult: GameFightTurnReadyMessage) {
        println("TURN READY")
    }

}