package fr.lewon.dofus.bot.sniffer.model.messages.game.guild.application

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildSubmitApplicationMessage : NetworkMessage() {
	var applyText: String = ""
	var guildId: Int = 0
	var timeSpent: Int = 0
	var filterLanguage: String = ""
	var filterAmbiance: String = ""
	var filterPlaytime: String = ""
	var filterInterest: String = ""
	var filterMinMaxGuildLevel: String = ""
	var filterRecruitmentType: String = ""
	var filterMinMaxCharacterLevel: String = ""
	var filterMinMaxAchievement: String = ""
	var filterSearchName: String = ""
	var filterLastSort: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		applyText = stream.readUTF()
		guildId = stream.readVarInt().toInt()
		timeSpent = stream.readVarInt().toInt()
		filterLanguage = stream.readUTF()
		filterAmbiance = stream.readUTF()
		filterPlaytime = stream.readUTF()
		filterInterest = stream.readUTF()
		filterMinMaxGuildLevel = stream.readUTF()
		filterRecruitmentType = stream.readUTF()
		filterMinMaxCharacterLevel = stream.readUTF()
		filterMinMaxAchievement = stream.readUTF()
		filterSearchName = stream.readUTF()
		filterLastSort = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 8897
}
