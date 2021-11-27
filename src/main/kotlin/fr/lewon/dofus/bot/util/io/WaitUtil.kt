package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore

object WaitUtil {

    const val DEFAULT_TIMEOUT_MILLIS = 60 * 1000

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
        vararg additionalEventClasses: Class<out INetworkMessage>,
        clearEventStore: Boolean = true,
        cancellationToken: CancellationToken,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ): Boolean {
        if (clearEventStore) {
            EventStore.clear(snifferId)
        }
        return waitUntil(
            { EventStore.containsSequence(snifferId, mainEventClass, *additionalEventClasses) },
            cancellationToken, timeout
        )
    }

    fun waitForSequence(
        snifferId: Long,
        mainEventClass: Class<out INetworkMessage>,
        additionalEventClassesWithCount: Map<Class<out INetworkMessage>, Int>,
        clearEventStore: Boolean = true,
        cancellationToken: CancellationToken,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ): Boolean {
        if (clearEventStore) {
            EventStore.clear(snifferId)
        }
        return waitUntil(
            { EventStore.containsSequence(snifferId, mainEventClass, additionalEventClassesWithCount) },
            cancellationToken, timeout
        )
    }

}