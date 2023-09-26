package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party.breach

import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party.AbstractPartyMemberInFightMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyMemberInBreachFightMessage : AbstractPartyMemberInFightMessage() {
	var floor: Int = 0
	var room: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		floor = stream.readVarInt().toInt()
		room = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 4267
}
