package fr.lewon.dofus.bot.sniffer.model.types.fight

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class FightStartingPositions : INetworkType {

    var positionsForChallenger = ArrayList<Int>()
    var positionsForDefenders = ArrayList<Int>()

    override fun deserialize(stream: ByteArrayReader) {
        for (i in 0 until stream.readShort()) {
            positionsForChallenger.add(stream.readVarShort())
        }
        for (i in 0 until stream.readShort()) {
            positionsForDefenders.add(stream.readVarShort())
        }
    }
}