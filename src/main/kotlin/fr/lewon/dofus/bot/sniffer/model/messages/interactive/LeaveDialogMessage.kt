package fr.lewon.dofus.bot.sniffer.model.messages.interactive

import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class LeaveDialogMessage : INetworkMessage {
    override fun deserialize(stream: ByteArrayReader) {
    }
}