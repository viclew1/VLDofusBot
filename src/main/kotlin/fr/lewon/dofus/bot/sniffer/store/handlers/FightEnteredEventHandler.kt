package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.sniffer.model.messages.GameRolePlayShowActorMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object FightEnteredEventHandler : EventHandler<GameRolePlayShowActorMessage> {

    override fun onEventReceived(socketResult: GameRolePlayShowActorMessage) {
        println("FIGHT ENTERED")
    }

}