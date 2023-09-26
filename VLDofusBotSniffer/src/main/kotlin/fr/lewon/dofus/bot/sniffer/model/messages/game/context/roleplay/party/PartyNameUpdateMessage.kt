package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyNameUpdateMessage : AbstractPartyMessage() {
	var partyName: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		partyName = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 8601
}
