package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance

import fr.lewon.dofus.bot.sniffer.model.types.game.alliance.AllianceMemberInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceMemberInformationUpdateMessage : NetworkMessage() {
	lateinit var member: AllianceMemberInfo
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		member = AllianceMemberInfo()
		member.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 2538
}
