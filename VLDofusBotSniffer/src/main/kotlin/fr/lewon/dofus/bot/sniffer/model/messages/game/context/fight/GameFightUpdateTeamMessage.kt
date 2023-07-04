package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightTeamInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightUpdateTeamMessage : NetworkMessage() {
	var fightId: Int = 0
	lateinit var team: FightTeamInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fightId = stream.readVarShort().toInt()
		team = FightTeamInformations()
		team.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 9852
}
