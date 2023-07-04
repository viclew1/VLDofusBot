package fr.lewon.dofus.bot.sniffer.model.messages.game.collector.tax

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorStateUpdateMessage : NetworkMessage() {
	var uniqueId: Double = 0.0
	var state: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		uniqueId = stream.readDouble().toDouble()
		state = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 9183
}
