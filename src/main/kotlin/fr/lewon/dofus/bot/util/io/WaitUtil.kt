package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.sniffer.model.INetworkType
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
            if (condition.invoke()) {
                return true
            }
        }
        return false
    }

    fun <T : INetworkType> waitForEvents(
        snifferId: Long,
        mainEventClass: Class<T>,
        vararg additionalEventClasses: Class<out INetworkType>,
        clearQueue: Boolean = true,
        cancellationToken: CancellationToken,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ): T {
        if (clearQueue) {
            additionalEventClasses.forEach { EventStore.clear(it, snifferId) }
        }
        val result = waitForEvent(snifferId, mainEventClass, clearQueue, cancellationToken, timeout)
        additionalEventClasses.forEach { waitForEvent(snifferId, it, false, cancellationToken, timeout) }
        return result
    }

    fun <T : INetworkType> waitForEvent(
        snifferId: Long,
        eventClass: Class<T>,
        clearQueue: Boolean = true,
        cancellationToken: CancellationToken,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ): T {
        var socketEvent: INetworkType? = null
        if (clearQueue) {
            EventStore.clear(eventClass, snifferId)
        }
        waitUntil(
            { EventStore.getLastEvent(eventClass, snifferId).also { socketEvent = it } != null },
            cancellationToken,
            timeout
        )
        socketEvent ?: error("No event of type [${eventClass.simpleName}] arrived in time ($timeout millis)")
        return eventClass.cast(socketEvent)
    }
}