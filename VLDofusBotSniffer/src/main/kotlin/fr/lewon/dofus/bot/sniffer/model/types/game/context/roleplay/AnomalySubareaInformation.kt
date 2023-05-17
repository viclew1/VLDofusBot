package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AnomalySubareaInformation : NetworkType() {
	var subAreaId: Int = 0
	var rewardRate: Int = 0
	var hasAnomaly: Boolean = false
	var anomalyClosingTime: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		subAreaId = stream.readVarShort().toInt()
		rewardRate = stream.readVarShort().toInt()
		hasAnomaly = stream.readBoolean()
		anomalyClosingTime = stream.readVarLong().toDouble()
	}
}
