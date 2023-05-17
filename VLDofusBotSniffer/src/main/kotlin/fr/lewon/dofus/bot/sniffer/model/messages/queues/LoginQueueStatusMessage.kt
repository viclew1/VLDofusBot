package fr.lewon.dofus.bot.sniffer.model.messages.queues

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class LoginQueueStatusMessage : NetworkMessage() {
	var position: Int = 0
	var total: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		position = stream.readUnsignedShort().toInt()
		total = stream.readUnsignedShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 2612
}
