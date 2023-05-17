package fr.lewon.dofus.bot.sniffer.store.waiters

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

abstract class AbstractMessageWaiter(val lock: ReentrantLock) {

    private val condition = lock.newCondition()
    var consumed = false
        private set

    protected fun notifyWaitingThread() {
        if (!consumed) {
            lock.executeSyncOperation {
                condition.signal()
                consumed = true
            }
        }
    }

    abstract fun onMessageReceived(message: NetworkMessage)

    fun waitUntilNotify(timeoutMillis: Long): Boolean {
        return condition.await(timeoutMillis, TimeUnit.MILLISECONDS)
    }

}