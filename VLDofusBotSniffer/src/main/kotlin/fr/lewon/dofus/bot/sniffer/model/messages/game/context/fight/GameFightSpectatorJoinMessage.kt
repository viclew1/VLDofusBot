package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.NamedPartyTeam
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightSpectatorJoinMessage : GameFightJoinMessage() {
	var namedPartyTeams: ArrayList<NamedPartyTeam> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		namedPartyTeams = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = NamedPartyTeam()
			item.deserialize(stream)
			namedPartyTeams.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 5315
}
