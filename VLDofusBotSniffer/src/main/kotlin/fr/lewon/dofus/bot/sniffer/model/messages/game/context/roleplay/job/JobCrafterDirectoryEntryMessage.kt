package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.job

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job.JobCrafterDirectoryEntryJobInfo
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job.JobCrafterDirectoryEntryPlayerInfo
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobCrafterDirectoryEntryMessage : NetworkMessage() {
	lateinit var playerInfo: JobCrafterDirectoryEntryPlayerInfo
	var jobInfoList: ArrayList<JobCrafterDirectoryEntryJobInfo> = ArrayList()
	lateinit var playerLook: EntityLook
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerInfo = JobCrafterDirectoryEntryPlayerInfo()
		playerInfo.deserialize(stream)
		jobInfoList = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = JobCrafterDirectoryEntryJobInfo()
			item.deserialize(stream)
			jobInfoList.add(item)
		}
		playerLook = EntityLook()
		playerLook.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 4972
}
