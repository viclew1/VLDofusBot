package fr.lewon.dofus.bot.sniffer.model.messages.fight

import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameFightPlacementPossiblePositionsMessage : INetworkMessage {

    val positionsForChallengers = ArrayList<Int>()
    val positionsForDefenders = ArrayList<Int>()
    var teamNumber = 0

    override fun deserialize(stream: ByteArrayReader) {
        for (i in 0 until stream.readUnsignedShort()) {
            positionsForChallengers.add(stream.readVarShort())
        }
        for (i in 0 until stream.readUnsignedShort()) {
            positionsForDefenders.add(stream.readVarShort())
        }
        teamNumber = stream.readByte().toInt()
    }
}