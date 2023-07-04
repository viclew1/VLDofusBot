package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightResultListEntry
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.NamedPartyTeamWithOutcome
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightEndMessage : NetworkMessage() {
	var duration: Int = 0
	var rewardRate: Int = 0
	var lootShareLimitMalus: Int = 0
	var results: ArrayList<FightResultListEntry> = ArrayList()
	var namedPartyTeamsOutcomes: ArrayList<NamedPartyTeamWithOutcome> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		duration = stream.readInt().toInt()
		rewardRate = stream.readVarShort().toInt()
		lootShareLimitMalus = stream.readUnsignedShort().toInt()
		results = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<FightResultListEntry>(stream.readUnsignedShort())
			item.deserialize(stream)
			results.add(item)
		}
		namedPartyTeamsOutcomes = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = NamedPartyTeamWithOutcome()
			item.deserialize(stream)
			namedPartyTeamsOutcomes.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1070
}
