package fr.lewon.dofus.bot.sniffer.model.types.fight.charac.impl

import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class CharacterCharacteristicValue : CharacterCharacteristic() {

    var total = 0

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        total = stream.readInt()
    }

}