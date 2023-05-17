package fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ObjectEffectDate : ObjectEffect() {
	var year: Int = 0
	var month: Int = 0
	var day: Int = 0
	var hour: Int = 0
	var minute: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		year = stream.readVarShort().toInt()
		month = stream.readUnsignedByte().toInt()
		day = stream.readUnsignedByte().toInt()
		hour = stream.readUnsignedByte().toInt()
		minute = stream.readUnsignedByte().toInt()
	}
}
