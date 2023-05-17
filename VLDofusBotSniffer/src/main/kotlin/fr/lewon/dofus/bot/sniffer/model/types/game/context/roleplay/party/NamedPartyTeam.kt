package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class NamedPartyTeam : NetworkType() {
	var teamId: Int = 0
	var partyName: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		teamId = stream.readUnsignedByte().toInt()
		partyName = stream.readUTF()
	}
}
