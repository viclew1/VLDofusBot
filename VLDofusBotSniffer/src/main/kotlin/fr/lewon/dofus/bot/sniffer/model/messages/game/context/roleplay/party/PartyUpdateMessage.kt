package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.PartyMemberInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyUpdateMessage : AbstractPartyEventMessage() {
	lateinit var memberInformations: PartyMemberInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		memberInformations = ProtocolTypeManager.getInstance<PartyMemberInformations>(stream.readUnsignedShort())
		memberInformations.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 9642
}
