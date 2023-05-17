package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ArenaRankInfos : NetworkType() {
	lateinit var ranking: ArenaRanking
	lateinit var leagueRanking: ArenaLeagueRanking
	var victoryCount: Int = 0
	var fightcount: Int = 0
	var numFightNeededForLadder: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		ranking = ArenaRanking()
		if (stream.readUnsignedByte() != 0) {
			ranking.deserialize(stream)
		}
		leagueRanking = ArenaLeagueRanking()
		if (stream.readUnsignedByte() != 0) {
			leagueRanking.deserialize(stream)
		}
		victoryCount = stream.readVarShort().toInt()
		fightcount = stream.readVarShort().toInt()
		numFightNeededForLadder = stream.readUnsignedShort().toInt()
	}
}
