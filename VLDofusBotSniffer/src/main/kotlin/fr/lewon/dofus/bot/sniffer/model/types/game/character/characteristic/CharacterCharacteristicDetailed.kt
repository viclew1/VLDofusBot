package fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterCharacteristicDetailed : CharacterCharacteristic() {
	var base: Int = 0
	var additional: Int = 0
	var objectsAndMountBonus: Int = 0
	var alignGiftBonus: Int = 0
	var contextModif: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		base = stream.readVarInt().toInt()
		additional = stream.readVarInt().toInt()
		objectsAndMountBonus = stream.readVarInt().toInt()
		alignGiftBonus = stream.readVarInt().toInt()
		contextModif = stream.readVarInt().toInt()
	}
}
