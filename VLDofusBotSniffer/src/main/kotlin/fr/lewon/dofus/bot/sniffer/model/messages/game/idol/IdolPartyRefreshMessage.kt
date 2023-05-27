package fr.lewon.dofus.bot.sniffer.model.messages.game.idol

import fr.lewon.dofus.bot.sniffer.model.types.game.idol.PartyIdol
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IdolPartyRefreshMessage : NetworkMessage() {
	lateinit var partyIdol: PartyIdol
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		partyIdol = PartyIdol()
		partyIdol.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 7021
}
