package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager

object WaitUtil {

    /**
     * Wait for a given time in ms.
     * @param time - Time to wait iin ms.
     */
    fun sleep(time: Long) {
        Thread.sleep(time)
    }

    /**
     * Wait for a given time in ms.
     * @param time - Time to wait iin ms.
     */
    fun sleep(time: Int) {
        sleep(time.toLong())
    }

    /**
     * Wait until the given condition returns true, returns true if the condition is verified before given timeOut
     */
    fun waitUntil(
        condition: () -> Boolean, timeOutMillis: Int = DTBConfigManager.config.globalTimeout * 1000
    ): Boolean {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeOutMillis) {
            if (condition.invoke()) {
                return true
            }
        }
        return false
    }

    /**
     * Waits for an event with timeout.
     * @param eventClass - Type of the event to wait for.
     * @param timeout - Timeout in ms.
     * @return The next received event corresponding to the type, null if timeout is reached.
     */
    fun <T : INetworkType> waitForEventWithoutError(
        eventClass: Class<T>,
        timeout: Int = DTBConfigManager.config.globalTimeout * 1000,
        clearQueue: Boolean = true
    ): T? {
        return try {
            waitForEvent(eventClass, timeout, clearQueue)
        } catch (e: Exception) {
            null
        }
    }

    fun <T : INetworkType> waitForEvents(
        mainEventClass: Class<T>,
        vararg additionalEventClasses: Class<out INetworkType>,
        timeout: Int = DTBConfigManager.config.globalTimeout * 1000,
        clearQueue: Boolean = true
    ): T {
        if (clearQueue) {
            additionalEventClasses.forEach { EventStore.clear(it) }
        }
        val result = waitForEvent(mainEventClass, timeout, clearQueue)
        additionalEventClasses.forEach { waitForEvent(it, timeout, false) }
        return result
    }

    /**
     * Waits for an event with timeout.
     * @param eventClass - Type of the event to wait for.
     * @param timeout - Timeout in ms.
     * @return The next received event corresponding to the type, error is timeout is reached.
     */
    fun <T : INetworkType> waitForEvent(
        eventClass: Class<T>,
        timeout: Int = DTBConfigManager.config.globalTimeout * 1000,
        clearQueue: Boolean = true
    ): T {
        var socketEvent: INetworkType? = null
        if (clearQueue) {
            EventStore.clear(eventClass)
        }
        waitUntil({ EventStore.getLastEvent(eventClass).also { socketEvent = it } != null }, timeout)
        socketEvent ?: error("No event of type [${eventClass.simpleName}] arrived in time ($timeout millis)")
        return eventClass.cast(socketEvent)
    }
}