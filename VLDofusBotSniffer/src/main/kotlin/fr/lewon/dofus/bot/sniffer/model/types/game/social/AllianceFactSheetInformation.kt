package fr.lewon.dofus.bot.sniffer.model.types.game.social

import fr.lewon.dofus.bot.sniffer.model.types.game.alliance.recruitment.AllianceRecruitmentInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.AllianceInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceFactSheetInformation : AllianceInformation() {
	var creationDate: Int = 0
	var nbMembers: Int = 0
	var nbSubarea: Int = 0
	var nbTaxCollectors: Int = 0
	lateinit var recruitment: AllianceRecruitmentInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		creationDate = stream.readInt().toInt()
		nbMembers = stream.readVarShort().toInt()
		nbSubarea = stream.readVarShort().toInt()
		nbTaxCollectors = stream.readVarShort().toInt()
		recruitment = AllianceRecruitmentInformation()
		recruitment.deserialize(stream)
	}
}
