package fr.lewon.dofus.bot.sniffer.model.types.fight

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class FightStartingPositions : INetworkType {

    var positionsForChallenger = ArrayList<Int>()
    var positionsForDefenders = ArrayList<Int>()

    override fun deserialize(stream: ByteArrayReader) {
        for (i in 0 until stream.readUnsignedShort()) {
            positionsForChallenger.add(stream.readVarShort())
        }
        for (i in 0 until stream.readUnsignedShort()) {
            positionsForDefenders.add(stream.readVarShort())
        }
    }
}