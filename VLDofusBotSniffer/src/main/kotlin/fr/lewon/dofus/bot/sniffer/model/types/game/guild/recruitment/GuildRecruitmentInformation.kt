package fr.lewon.dofus.bot.sniffer.model.types.game.guild.recruitment

import fr.lewon.dofus.bot.sniffer.model.types.game.social.recruitment.SocialRecruitmentInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildRecruitmentInformation : SocialRecruitmentInformation() {
	var minSuccess: Int = 0
	var minSuccessFacultative: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		minSuccess = stream.readVarInt().toInt()
		minSuccessFacultative = stream.readBoolean()
	}
}
