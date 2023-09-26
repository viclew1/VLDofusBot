package fr.lewon.dofus.bot.sniffer.model.messages.game.basic

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BasicLatencyStatsMessage : NetworkMessage() {
	var latency: Int = 0
	var sampleCount: Int = 0
	var max: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		latency = stream.readUnsignedShort().toInt()
		sampleCount = stream.readVarShort().toInt()
		max = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 2623
}
