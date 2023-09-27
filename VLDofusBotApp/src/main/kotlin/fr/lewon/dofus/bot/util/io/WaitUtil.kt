package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.util.network.info.GameInfo

object WaitUtil {

    const val DEFAULT_TIMEOUT_MILLIS = 25 * 1000

    fun sleep(time: Long) {
        Thread.sleep(time)
    }

    fun sleep(time: Int) {
        sleep(time.toLong())
    }

    fun waitUntil(timeOutMillis: Int = DEFAULT_TIMEOUT_MILLIS, condition: () -> Boolean): Boolean {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeOutMillis) {
            if (condition()) {
                return true
            }
            sleep(100)
        }
        return false
    }

    fun <T : NetworkMessage> waitForEvent(
        gameInfo: GameInfo,
        messageClass: Class<T>,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ): T {
        waitUntil(timeout) { gameInfo.eventStore.getFirstEvent(messageClass) != null }
        return gameInfo.eventStore.getFirstEvent(messageClass)
            ?: error(getErrorMessage(messageClass))
    }

    fun waitForEvents(
        gameInfo: GameInfo,
        vararg messageClasses: Class<out NetworkMessage>,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS,
    ) {
        if (!waitUntil(timeout) { gameInfo.eventStore.isAllEventsPresent(*messageClasses) }) {
            error(getErrorMessage(*messageClasses))
        }
    }

    fun waitForAnyEvent(
        gameInfo: GameInfo,
        vararg messageClasses: Class<out NetworkMessage>,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS,
    ) {
        if (!waitUntil(timeout) { messageClasses.any { gameInfo.eventStore.getLastEvent(it) != null } }) {
            error(getErrorMessage(*messageClasses))
        }
    }

    fun waitUntilMessageArrives(
        gameInfo: GameInfo,
        messageClass: Class<out NetworkMessage>,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ) {
        if (!gameInfo.eventStore.waitUntilMessagesArrives(messageClass, timeout)) {
            error(getErrorMessage(messageClass))
        }
    }

    fun waitUntilAnyMessageArrives(
        gameInfo: GameInfo,
        vararg messageClasses: Class<out NetworkMessage>,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ) {
        if (!gameInfo.eventStore.waitUntilAnyMessageArrives(messageClasses, timeout)) {
            error(getErrorMessage(*messageClasses))
        }
    }

    fun waitUntilMultipleMessagesArrive(
        gameInfo: GameInfo,
        vararg messageClasses: Class<out NetworkMessage>,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ) {
        if (!gameInfo.eventStore.waitUntilMultipleMessagesArrive(messageClasses, timeout)) {
            error(getErrorMessage(*messageClasses))
        }
    }

    fun waitUntilOrderedMessagesArrive(
        gameInfo: GameInfo,
        vararg messageClasses: Class<out NetworkMessage>,
        timeout: Int = DEFAULT_TIMEOUT_MILLIS
    ) {
        if (!gameInfo.eventStore.waitUntilOrderedMessagesArrive(messageClasses, timeout)) {
            error(getErrorMessage(*messageClasses))
        }
    }

    private fun getErrorMessage(vararg messageClasses: Class<out NetworkMessage>): String {
        val messageClassesStr = messageClasses.joinToString(", ") { it.simpleName }
        return "Not all messages [$messageClassesStr] arrived in time."
    }

    private fun getErrorMessage(messageClass: Class<out NetworkMessage>): String {
        return "No message [${messageClass.typeName}] arrived in time."
    }

}