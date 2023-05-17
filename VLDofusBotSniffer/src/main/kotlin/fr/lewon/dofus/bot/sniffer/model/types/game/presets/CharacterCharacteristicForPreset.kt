package fr.lewon.dofus.bot.sniffer.model.types.game.presets

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterCharacteristicForPreset : SimpleCharacterCharacteristicForPreset() {
	var stuff: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		stuff = stream.readVarInt().toInt()
	}
}
