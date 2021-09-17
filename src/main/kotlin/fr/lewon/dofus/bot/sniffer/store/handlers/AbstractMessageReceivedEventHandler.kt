package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.sniffer.model.messages.chat.ChatServerMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

abstract class AbstractMessageReceivedEventHandler<T : ChatServerMessage> : EventHandler<T> {

    override fun onEventReceived(socketResult: T) {
        ConsoleLogger.debug("(${socketResult.channel}) ${socketResult.pseudo} : ${socketResult.text}")
    }

}