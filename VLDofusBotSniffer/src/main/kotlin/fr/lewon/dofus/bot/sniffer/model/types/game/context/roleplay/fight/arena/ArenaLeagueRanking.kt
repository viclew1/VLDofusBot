package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ArenaLeagueRanking : NetworkType() {
	var rank: Int = 0
	var leagueId: Int = 0
	var leaguePoints: Int = 0
	var totalLeaguePoints: Int = 0
	var ladderPosition: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		rank = stream.readVarShort().toInt()
		leagueId = stream.readVarShort().toInt()
		leaguePoints = stream.readVarShort().toInt()
		totalLeaguePoints = stream.readVarShort().toInt()
		ladderPosition = stream.readInt().toInt()
	}
}
