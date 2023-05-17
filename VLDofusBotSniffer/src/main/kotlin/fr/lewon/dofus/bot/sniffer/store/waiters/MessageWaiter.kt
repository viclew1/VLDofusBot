package fr.lewon.dofus.bot.sniffer.store.waiters

import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import java.util.concurrent.locks.ReentrantLock

class MessageWaiter(lock: ReentrantLock, private val messageClass: Class<out NetworkMessage>) :
    AbstractMessageWaiter(lock) {

    override fun onMessageReceived(message: NetworkMessage) {
        if (messageClass == message::class.java) {
            notifyWaitingThread()
        }
    }

}