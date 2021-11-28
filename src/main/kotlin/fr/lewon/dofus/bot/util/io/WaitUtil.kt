package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore

object WaitUtil {

    const val DEFAULT_TIMEOUT_MILLIS = 25 * 1000

    fun sleep(time: Long) {
        Thread.sleep(time)
    }

    fun sleep(time: Int) {
        sleep(time.toLong())
    }

    fun waitUntil(
        condition: () -> Boolean,
        cancellationToken: CancellationToken,
        timeOutMillis: Int = DEFAULT_TIMEOUT_MILLIS
    ): Boolean {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeOutMillis) {
            cancellationToken.checkCancel()
            if (condition()) {
                return true
            }
        }
        return false
    }

    fun waitForSequence(
        snifferId: Long,
        mainEventClass: Class<out INetworkMessage>,
        endEventClass: Class<out INetworkMessage>,
        clearEventStore: Boolean = true,
        removeWhenFound: Boolean = true,
        cancellationToken: CancellationToken,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ): Boolean {
        if (clearEventStore) {
            EventStore.clear(snifferId)
        }
        val containsSequenceFunc = {
            EventStore.containsSequence(
                snifferId,
                mainEventClass,
                endEventClass,
            )
        }
        if (!waitUntil({ containsSequenceFunc() }, cancellationToken, timeout)) {
            error("Sequence not found in time. Events in store : ${EventStore.getStoredEventsStr(snifferId)}")
        }
        if (removeWhenFound) {
            EventStore.removeSequence(
                snifferId,
                mainEventClass,
                endEventClass
            )
        }
        return true
    }

    fun <T : INetworkMessage> waitForEvent(
        snifferId: Long,
        messageClass: Class<T>,
        cancellationToken: CancellationToken,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ): T? {
        waitUntil({ EventStore.getLastEvent(messageClass, snifferId) != null }, cancellationToken, timeout)
        return EventStore.getLastEvent(messageClass, snifferId)
    }

}