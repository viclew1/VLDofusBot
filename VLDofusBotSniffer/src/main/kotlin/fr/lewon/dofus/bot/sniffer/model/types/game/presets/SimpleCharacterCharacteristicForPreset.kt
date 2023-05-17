package fr.lewon.dofus.bot.sniffer.model.types.game.presets

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SimpleCharacterCharacteristicForPreset : NetworkType() {
	var keyword: String = ""
	var base: Int = 0
	var additionnal: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		keyword = stream.readUTF()
		base = stream.readVarInt().toInt()
		additionnal = stream.readVarInt().toInt()
	}
}
