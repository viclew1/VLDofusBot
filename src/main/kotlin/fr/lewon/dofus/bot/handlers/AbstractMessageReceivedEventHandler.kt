package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.model.messages.chat.ChatServerMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.core.logs.VldbLogger

abstract class AbstractMessageReceivedEventHandler<T : ChatServerMessage> : EventHandler<T> {

    override fun onEventReceived(socketResult: T) {
        VldbLogger.debug("(${socketResult.channel}) ${socketResult.pseudo} : ${socketResult.text}")
    }

}