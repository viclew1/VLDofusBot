package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.job

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job.JobCrafterDirectorySettings
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobCrafterDirectorySettingsMessage : NetworkMessage() {
	var craftersSettings: ArrayList<JobCrafterDirectorySettings> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		craftersSettings = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = JobCrafterDirectorySettings()
			item.deserialize(stream)
			craftersSettings.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4121
}
