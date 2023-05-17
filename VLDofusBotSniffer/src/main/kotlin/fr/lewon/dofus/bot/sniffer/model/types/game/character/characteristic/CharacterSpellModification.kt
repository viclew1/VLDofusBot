package fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterSpellModification : NetworkType() {
	var modificationType: Int = 0
	var spellId: Int = 0
	lateinit var value: CharacterCharacteristicDetailed
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		modificationType = stream.readUnsignedByte().toInt()
		spellId = stream.readVarShort().toInt()
		value = CharacterCharacteristicDetailed()
		value.deserialize(stream)
	}
}
