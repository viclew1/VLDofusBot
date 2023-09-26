package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.job

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job.JobCrafterDirectoryListEntry
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobCrafterDirectoryListMessage : NetworkMessage() {
	var listEntries: ArrayList<JobCrafterDirectoryListEntry> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		listEntries = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = JobCrafterDirectoryListEntry()
			item.deserialize(stream)
			listEntries.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 5366
}
