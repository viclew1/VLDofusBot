package fr.lewon.dofus.bot.sniffer.model.messages.game.basic

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BasicDateMessage : NetworkMessage() {
	var day: Int = 0
	var month: Int = 0
	var year: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		day = stream.readUnsignedByte().toInt()
		month = stream.readUnsignedByte().toInt()
		year = stream.readUnsignedShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 8296
}
