package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.job

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job.JobCrafterDirectorySettings
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobCrafterDirectoryDefineSettingsMessage : NetworkMessage() {
	lateinit var settings: JobCrafterDirectorySettings
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		settings = JobCrafterDirectorySettings()
		settings.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 3149
}
