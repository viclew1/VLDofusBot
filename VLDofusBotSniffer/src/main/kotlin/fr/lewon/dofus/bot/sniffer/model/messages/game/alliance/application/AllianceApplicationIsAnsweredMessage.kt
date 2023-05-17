package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance.application

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.AllianceInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceApplicationIsAnsweredMessage : NetworkMessage() {
	var accepted: Boolean = false
	lateinit var allianceInformation: AllianceInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		accepted = stream.readBoolean()
		allianceInformation = AllianceInformation()
		allianceInformation.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 1445
}
