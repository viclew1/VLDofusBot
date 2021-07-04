package fr.lewon.dofus.bot.sniffer.model.types.actor.entity

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class EntityDispositionInformations : INetworkType {

    var cellId = -1
    var direction = -1
    var carryingCharacterId = -1.0

    override fun deserialize(stream: ByteArrayReader) {
        cellId = stream.readShort()
        direction = stream.readByte().toInt()
    }

}