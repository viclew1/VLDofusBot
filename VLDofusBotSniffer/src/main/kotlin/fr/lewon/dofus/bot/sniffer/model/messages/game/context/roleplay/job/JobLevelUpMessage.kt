package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.job

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job.JobDescription
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobLevelUpMessage : NetworkMessage() {
	var newLevel: Int = 0
	lateinit var jobsDescription: JobDescription
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		newLevel = stream.readUnsignedByte().toInt()
		jobsDescription = JobDescription()
		jobsDescription.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 7209
}
