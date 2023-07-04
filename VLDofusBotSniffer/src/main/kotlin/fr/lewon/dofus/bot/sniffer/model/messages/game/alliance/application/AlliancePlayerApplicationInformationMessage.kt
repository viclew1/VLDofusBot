package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance.application

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.AllianceInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.social.application.SocialApplicationInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AlliancePlayerApplicationInformationMessage : AlliancePlayerApplicationAbstractMessage() {
	lateinit var allianceInformation: AllianceInformation
	lateinit var apply: SocialApplicationInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		allianceInformation = AllianceInformation()
		allianceInformation.deserialize(stream)
		apply = SocialApplicationInformation()
		apply.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 96
}
