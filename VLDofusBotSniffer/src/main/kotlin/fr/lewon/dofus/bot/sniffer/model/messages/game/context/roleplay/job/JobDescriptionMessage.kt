package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.job

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job.JobDescription
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobDescriptionMessage : NetworkMessage() {
	var jobsDescription: ArrayList<JobDescription> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		jobsDescription = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = JobDescription()
			item.deserialize(stream)
			jobsDescription.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 3482
}
