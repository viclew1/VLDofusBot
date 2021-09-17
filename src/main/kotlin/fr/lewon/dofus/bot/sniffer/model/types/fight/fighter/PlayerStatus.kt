package fr.lewon.dofus.bot.sniffer.model.types.fight.fighter

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class PlayerStatus : INetworkType {

    var statusId = 0

    override fun deserialize(stream: ByteArrayReader) {
        statusId = stream.readByte().toInt()
    }
}