package fr.lewon.dofus.bot.sniffer.model.types.game.social

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GuildInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.guild.recruitment.GuildRecruitmentInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildFactSheetInformations : GuildInformations() {
	var leaderId: Double = 0.0
	var nbMembers: Int = 0
	var lastActivityDay: Int = 0
	lateinit var recruitment: GuildRecruitmentInformation
	var nbPendingApply: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		leaderId = stream.readVarLong().toDouble()
		nbMembers = stream.readVarShort().toInt()
		lastActivityDay = stream.readUnsignedShort().toInt()
		recruitment = GuildRecruitmentInformation()
		recruitment.deserialize(stream)
		nbPendingApply = stream.readInt().toInt()
	}
}
