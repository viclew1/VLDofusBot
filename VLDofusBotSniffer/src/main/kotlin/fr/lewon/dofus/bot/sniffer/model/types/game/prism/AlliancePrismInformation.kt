package fr.lewon.dofus.bot.sniffer.model.types.game.prism

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.AllianceInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AlliancePrismInformation : PrismInformation() {
	lateinit var alliance: AllianceInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		alliance = AllianceInformation()
		alliance.deserialize(stream)
	}
}
