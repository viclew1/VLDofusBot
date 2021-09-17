package fr.lewon.dofus.bot.sniffer.model.messages.misc

import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class BasicNoOperationMessage : INetworkMessage {
    override fun deserialize(stream: ByteArrayReader) {
    }
}