package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobCrafterDirectoryListEntry : NetworkType() {
	lateinit var playerInfo: JobCrafterDirectoryEntryPlayerInfo
	lateinit var jobInfo: JobCrafterDirectoryEntryJobInfo
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerInfo = JobCrafterDirectoryEntryPlayerInfo()
		playerInfo.deserialize(stream)
		jobInfo = JobCrafterDirectoryEntryJobInfo()
		jobInfo.deserialize(stream)
	}
}
