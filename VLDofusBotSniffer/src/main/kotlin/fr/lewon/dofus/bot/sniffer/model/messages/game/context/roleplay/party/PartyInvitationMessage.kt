package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyInvitationMessage : AbstractPartyMessage() {
	var partyType: Int = 0
	var partyName: String = ""
	var maxParticipants: Int = 0
	var fromId: Double = 0.0
	var fromName: String = ""
	var toId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		partyType = stream.readUnsignedByte().toInt()
		partyName = stream.readUTF()
		maxParticipants = stream.readUnsignedByte().toInt()
		fromId = stream.readVarLong().toDouble()
		fromName = stream.readUTF()
		toId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 5425
}
