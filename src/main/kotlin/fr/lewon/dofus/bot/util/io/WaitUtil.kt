package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.util.network.GameInfo

object WaitUtil {

    const val DEFAULT_TIMEOUT_MILLIS = 25 * 1000

    fun sleep(time: Long) {
        Thread.sleep(time)
    }

    fun sleep(time: Int) {
        sleep(time.toLong())
    }

    fun waitUntil(condition: () -> Boolean, timeOutMillis: Int = DEFAULT_TIMEOUT_MILLIS): Boolean {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeOutMillis) {
            if (condition()) {
                return true
            }
            sleep(100)
        }
        return false
    }

    fun <T : INetworkMessage> waitForEvent(
        gameInfo: GameInfo,
        messageClass: Class<T>,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ): T {
        waitUntil({ gameInfo.eventStore.getFirstEvent(messageClass) != null }, timeout)
        return gameInfo.eventStore.getFirstEvent(messageClass)
            ?: error(getErrorMessage(messageClass))
    }

    fun waitForEvents(
        gameInfo: GameInfo,
        vararg messageClasses: Class<out INetworkMessage>,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS,
    ) {
        if (!waitUntil({ gameInfo.eventStore.isAllEventsPresent(*messageClasses) }, timeout)) {
            error(getErrorMessage(*messageClasses))
        }
    }

    fun waitUntilMessageArrives(
        gameInfo: GameInfo,
        messageClass: Class<out INetworkMessage>,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ) {
        if (!gameInfo.eventStore.waitUntilMessagesArrives(messageClass, timeout)) {
            error(getErrorMessage(messageClass))
        }
    }

    fun waitUntilMultipleMessagesArrive(
        gameInfo: GameInfo,
        vararg messageClasses: Class<out INetworkMessage>,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ) {
        if (!gameInfo.eventStore.waitUntilMultipleMessagesArrive(messageClasses, timeout)) {
            error(getErrorMessage(*messageClasses))
        }
    }

    fun waitUntilOrderedMessagesArrive(
        gameInfo: GameInfo,
        vararg messageClasses: Class<out INetworkMessage>,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ) {
        if (!gameInfo.eventStore.waitUntilOrderedMessagesArrive(messageClasses, timeout)) {
            error(getErrorMessage(*messageClasses))
        }
    }

    private fun getErrorMessage(vararg messageClasses: Class<out INetworkMessage>): String {
        val messageClassesStr = messageClasses.joinToString(", ") { it.simpleName }
        return "Not all messages [$messageClassesStr] arrived in time."
    }

    private fun getErrorMessage(messageClass: Class<out INetworkMessage>): String {
        return "No message [${messageClass.typeName}] arrived in time."
    }

}