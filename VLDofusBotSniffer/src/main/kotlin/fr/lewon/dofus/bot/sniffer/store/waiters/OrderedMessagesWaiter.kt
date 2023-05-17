package fr.lewon.dofus.bot.sniffer.store.waiters

import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import java.util.concurrent.locks.ReentrantLock

class OrderedMessagesWaiter(lock: ReentrantLock, messageClasses: Array<out Class<out NetworkMessage>>) :
    AbstractMessageWaiter(lock) {

    private val remainingMessageClasses = messageClasses.toMutableList()

    override fun onMessageReceived(message: NetworkMessage) {
        if (remainingMessageClasses.firstOrNull() == message::class.java) {
            remainingMessageClasses.removeAt(0)
            if (remainingMessageClasses.isEmpty()) {
                notifyWaitingThread()
            }
        }
    }

}