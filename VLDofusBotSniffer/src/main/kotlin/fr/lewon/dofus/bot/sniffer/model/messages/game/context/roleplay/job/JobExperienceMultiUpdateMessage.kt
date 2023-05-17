package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.job

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job.JobExperience
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobExperienceMultiUpdateMessage : NetworkMessage() {
	var experiencesUpdate: ArrayList<JobExperience> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		experiencesUpdate = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = JobExperience()
			item.deserialize(stream)
			experiencesUpdate.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 7720
}
