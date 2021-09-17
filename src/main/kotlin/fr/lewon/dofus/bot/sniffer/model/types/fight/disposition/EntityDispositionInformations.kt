package fr.lewon.dofus.bot.sniffer.model.types.fight.disposition

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

open class EntityDispositionInformations : INetworkType {

    var cellId = 0
    var direction = 0

    override fun deserialize(stream: ByteArrayReader) {
        cellId = stream.readUnsignedShort()
        direction = stream.readByte().toInt()
    }
}