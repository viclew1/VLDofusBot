package fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ObjectEffectDuration : ObjectEffect() {
	var days: Int = 0
	var hours: Int = 0
	var minutes: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		days = stream.readVarShort().toInt()
		hours = stream.readUnsignedByte().toInt()
		minutes = stream.readUnsignedByte().toInt()
	}
}
