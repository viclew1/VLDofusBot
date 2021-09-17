package fr.lewon.dofus.bot.sniffer.model.types.fight.charac

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class CharacterCharacteristics : INetworkType {

    var characteristics = ArrayList<CharacterCharacteristic>()

    override fun deserialize(stream: ByteArrayReader) {
        for (i in 0 until stream.readUnsignedShort()) {
            val id = stream.readUnsignedShort()
            val characteristic = TypeManager.getInstance<CharacterCharacteristic>(id)
            characteristic.deserialize(stream)
            characteristics.add(characteristic)
        }
    }
}