package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class NamedPartyTeamWithOutcome : NetworkType() {
	lateinit var team: NamedPartyTeam
	var outcome: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		team = NamedPartyTeam()
		team.deserialize(stream)
		outcome = stream.readVarShort().toInt()
	}
}
