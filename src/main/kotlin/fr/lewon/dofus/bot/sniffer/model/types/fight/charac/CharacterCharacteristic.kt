package fr.lewon.dofus.bot.sniffer.model.types.fight.charac

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

open class CharacterCharacteristic : INetworkType {

    var characteristicId = 0

    override fun deserialize(stream: ByteArrayReader) {
        characteristicId = stream.readUnsignedShort()
    }
}