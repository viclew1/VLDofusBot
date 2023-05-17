package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance.recruitment

import fr.lewon.dofus.bot.sniffer.model.types.game.alliance.recruitment.AllianceRecruitmentInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceUpdateRecruitmentInformationMessage : NetworkMessage() {
	lateinit var recruitmentData: AllianceRecruitmentInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		recruitmentData = AllianceRecruitmentInformation()
		recruitmentData.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 4203
}
