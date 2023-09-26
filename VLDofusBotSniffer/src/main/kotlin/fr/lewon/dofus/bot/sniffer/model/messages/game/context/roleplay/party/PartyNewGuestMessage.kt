package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.PartyGuestInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyNewGuestMessage : AbstractPartyEventMessage() {
	lateinit var guest: PartyGuestInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guest = PartyGuestInformations()
		guest.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 3268
}
