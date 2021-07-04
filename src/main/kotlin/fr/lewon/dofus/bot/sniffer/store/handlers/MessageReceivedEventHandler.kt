package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.sniffer.model.messages.ChatServerMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object MessageReceivedEventHandler : EventHandler<ChatServerMessage> {

    override fun onEventReceived(socketResult: ChatServerMessage) {
        println("Message received : ")
        println("(${socketResult.channel}) ${socketResult.pseudo} : ${socketResult.text}")
    }

}