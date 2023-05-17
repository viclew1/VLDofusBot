package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyCancelInvitationNotificationMessage : AbstractPartyEventMessage() {
	var cancelerId: Double = 0.0
	var guestId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		cancelerId = stream.readVarLong().toDouble()
		guestId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 9907
}
