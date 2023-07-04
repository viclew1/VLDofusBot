package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.job

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job.JobCrafterDirectoryListEntry
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobCrafterDirectoryAddMessage : NetworkMessage() {
	lateinit var listEntry: JobCrafterDirectoryListEntry
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		listEntry = JobCrafterDirectoryListEntry()
		listEntry.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 2994
}
