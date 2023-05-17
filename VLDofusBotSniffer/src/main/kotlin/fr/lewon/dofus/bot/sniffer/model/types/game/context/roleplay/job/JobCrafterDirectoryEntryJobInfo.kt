package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobCrafterDirectoryEntryJobInfo : NetworkType() {
	var jobId: Int = 0
	var jobLevel: Int = 0
	var free: Boolean = false
	var minLevel: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		jobId = stream.readUnsignedByte().toInt()
		jobLevel = stream.readUnsignedByte().toInt()
		free = stream.readBoolean()
		minLevel = stream.readUnsignedByte().toInt()
	}
}
