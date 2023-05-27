package fr.lewon.dofus.bot.sniffer.model.messages.connection

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IdentificationFailedBannedMessage : IdentificationFailedMessage() {
	var banEndDate: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		banEndDate = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 1044
}
