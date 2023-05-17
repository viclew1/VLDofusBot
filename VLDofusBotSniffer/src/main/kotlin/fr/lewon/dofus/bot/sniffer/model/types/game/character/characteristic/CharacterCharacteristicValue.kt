package fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterCharacteristicValue : CharacterCharacteristic() {
	var total: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		total = stream.readInt().toInt()
	}
}
