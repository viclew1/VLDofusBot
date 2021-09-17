package fr.lewon.dofus.bot.sniffer

import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.util.ui.ConsoleLogger
import java.text.SimpleDateFormat
import java.util.*

class MessageReceiver(
    val eventName: String,
    val eventId: Int,
    val eventClass: Class<out INetworkMessage>?,
    val stream: ByteArrayReader
) {

    fun build(): INetworkMessage? {
        printMessageTreatment()
        return eventClass?.getConstructor()?.newInstance()
            ?.also { it.deserialize(stream) }
    }

    private fun printMessageTreatment() {
        val ts = SimpleDateFormat("HH:mm:ss.SSSXXX").format(Date())
        val untreatedStr = if (eventClass == null) "[UNTREATED] " else ""
        ConsoleLogger.debug("${untreatedStr}Message received : $ts - [$eventName:$eventId]")
    }

}