package fr.lewon.dofus.bot.sniffer.model.messages.game.guild.recruitment

import fr.lewon.dofus.bot.sniffer.model.types.game.guild.recruitment.GuildRecruitmentInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class UpdateRecruitmentInformationMessage : NetworkMessage() {
	lateinit var recruitmentData: GuildRecruitmentInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		recruitmentData = GuildRecruitmentInformation()
		recruitmentData.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 117
}
