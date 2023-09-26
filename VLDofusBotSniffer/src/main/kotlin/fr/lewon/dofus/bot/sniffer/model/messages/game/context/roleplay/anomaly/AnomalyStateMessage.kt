package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.anomaly

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AnomalyStateMessage : NetworkMessage() {
	var subAreaId: Int = 0
	var open: Boolean = false
	var closingTime: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		subAreaId = stream.readVarShort().toInt()
		open = stream.readBoolean()
		closingTime = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 8450
}
