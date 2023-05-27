package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildInvitationStateRecruterMessage : NetworkMessage() {
	var recrutedName: String = ""
	var invitationState: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		recrutedName = stream.readUTF()
		invitationState = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 2616
}
