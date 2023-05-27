package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.job

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job.JobExperience
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobExperienceOtherPlayerUpdateMessage : JobExperienceUpdateMessage() {
	var playerId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 3114
}
