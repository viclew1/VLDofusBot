package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayArenaLeagueRewardsMessage : NetworkMessage() {
	var seasonId: Int = 0
	var leagueId: Int = 0
	var ladderPosition: Int = 0
	var endSeasonReward: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		seasonId = stream.readVarShort().toInt()
		leagueId = stream.readVarShort().toInt()
		ladderPosition = stream.readInt().toInt()
		endSeasonReward = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 8806
}
