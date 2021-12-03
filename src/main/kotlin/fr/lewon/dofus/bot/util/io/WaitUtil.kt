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

    fun <T : INetworkMessage> waitForEvent(
        snifferId: Long,
        messageClass: Class<T>,
        cancellationToken: CancellationToken,
        removeWhenFound: Boolean = true,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ): T {
        waitUntil({ EventStore.getFirstEvent(messageClass, snifferId) != null }, cancellationToken, timeout)
        val event = EventStore.getFirstEvent(messageClass, snifferId)
            ?: error("No message [${messageClass.typeName}] arrived in time. ${EventStore.getStoredEventsStr(snifferId)}")
        if (removeWhenFound) {
            EventStore.removeEvent(messageClass, snifferId, 1)
        }
        return event
    }

    fun waitForEvents(
        snifferId: Long,
        vararg messageClasses: Class<out INetworkMessage>,
        cancellationToken: CancellationToken,
        removeWhenFound: Boolean = true,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS,
    ) {
        if (!waitUntil({ EventStore.isAllEventsPresent(snifferId, *messageClasses) }, cancellationToken, timeout)) {
            val messageClassesStr = messageClasses.joinToString(", ") { it.simpleName }
            error("Not all messages [$messageClassesStr] arrived in time. ${EventStore.getStoredEventsStr(snifferId)}")
        }
        if (removeWhenFound) {
            messageClasses.forEach { EventStore.removeEvent(it, snifferId, 1) }
        }
    }

}