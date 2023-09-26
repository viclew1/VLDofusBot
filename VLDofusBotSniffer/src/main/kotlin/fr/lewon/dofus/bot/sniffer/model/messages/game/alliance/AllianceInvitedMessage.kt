package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.AllianceInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceInvitedMessage : NetworkMessage() {
	var recruterName: String = ""
	lateinit var allianceInfo: AllianceInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		recruterName = stream.readUTF()
		allianceInfo = AllianceInformation()
		allianceInfo.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 4184
}
