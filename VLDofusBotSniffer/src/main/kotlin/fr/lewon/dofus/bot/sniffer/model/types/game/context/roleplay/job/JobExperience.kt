package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobExperience : NetworkType() {
	var jobId: Int = 0
	var jobLevel: Int = 0
	var jobXP: Double = 0.0
	var jobXpLevelFloor: Double = 0.0
	var jobXpNextLevelFloor: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		jobId = stream.readUnsignedByte().toInt()
		jobLevel = stream.readUnsignedByte().toInt()
		jobXP = stream.readVarLong().toDouble()
		jobXpLevelFloor = stream.readVarLong().toDouble()
		jobXpNextLevelFloor = stream.readVarLong().toDouble()
	}
}
