package fr.lewon.dofus.bot.sniffer.model.messages.move

import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameMapMovementMessage : INetworkMessage {

    val keyMovements = ArrayList<Int>()
    var forcedDirection = 0
    var actorId = 0.0

    override fun deserialize(stream: ByteArrayReader) {
        for (i in 0 until stream.readUnsignedShort()) {
            keyMovements.add(stream.readUnsignedShort())
        }
        forcedDirection = stream.readUnsignedShort()
        actorId = stream.readDouble()
    }
}