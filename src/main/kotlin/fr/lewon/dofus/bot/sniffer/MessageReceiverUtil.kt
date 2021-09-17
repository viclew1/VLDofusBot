package fr.lewon.dofus.bot.sniffer

import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.util.filemanagers.DTBDofusMessageManager
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader
import org.reflections.Reflections

object MessageReceiverUtil {


    private val messagesById = Reflections(INetworkMessage::class.java.packageName)
        .getSubTypesOf(INetworkMessage::class.java)
        .map { (DTBDofusMessageManager.getId(it.simpleName) ?: error("Couldn't find id for [${it.simpleName}]")) to it }
        .toMap()

    fun parse(stream: ByteArrayReader, messageId: Int): MessageReceiver? {
        val messageType = messagesById[messageId]
        val messageName = DTBDofusMessageManager.getName(messageId) ?: return null
        return MessageReceiver(messageName, messageId, messageType, stream)
    }

}