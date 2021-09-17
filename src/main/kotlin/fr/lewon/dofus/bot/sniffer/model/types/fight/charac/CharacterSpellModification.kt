package fr.lewon.dofus.bot.sniffer.model.types.fight.charac

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.impl.CharacterCharacteristicDetailed
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class CharacterSpellModification : INetworkType {

    var modificationType = 0
    var spellId = 0
    lateinit var value: CharacterCharacteristicDetailed

    override fun deserialize(stream: ByteArrayReader) {
        modificationType = stream.readByte().toInt()
        spellId = stream.readVarShort()
        value = CharacterCharacteristicDetailed()
        value.deserialize(stream)
    }
}