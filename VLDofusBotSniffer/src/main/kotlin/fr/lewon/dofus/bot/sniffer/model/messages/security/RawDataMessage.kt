package fr.lewon.dofus.bot.sniffer.model.messages.security

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class RawDataMessage : NetworkMessage() {
	var content: ByteArray = ByteArray(0)
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val contentLength = stream.readVarInt()
		content += stream.readNBytes(contentLength)
	}
	override fun getNetworkMessageId(): Int = 6253
}
