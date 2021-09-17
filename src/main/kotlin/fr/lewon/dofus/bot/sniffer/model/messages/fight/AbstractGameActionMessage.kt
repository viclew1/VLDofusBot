package fr.lewon.dofus.bot.sniffer.model.messages.fight

import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

open class AbstractGameActionMessage : INetworkMessage {

    var actionId = 0
    var sourceId = 0.0

    override fun deserialize(stream: ByteArrayReader) {
        actionId = stream.readVarShort()
        sourceId = stream.readDouble()
    }
}