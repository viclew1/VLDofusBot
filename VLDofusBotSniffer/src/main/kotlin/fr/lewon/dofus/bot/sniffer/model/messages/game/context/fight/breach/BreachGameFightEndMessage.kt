package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.breach

import fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.GameFightEndMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightResultListEntry
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.NamedPartyTeamWithOutcome
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BreachGameFightEndMessage : GameFightEndMessage() {
	var budget: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		budget = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 3085
}
