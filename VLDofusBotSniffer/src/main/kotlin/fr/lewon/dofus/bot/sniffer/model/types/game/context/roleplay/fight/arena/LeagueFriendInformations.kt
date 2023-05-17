package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.friend.AbstractContactInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class LeagueFriendInformations : AbstractContactInformations() {
	var playerId: Double = 0.0
	var playerName: String = ""
	var breed: Int = 0
	var sex: Boolean = false
	var level: Int = 0
	var leagueId: Int = 0
	var totalLeaguePoints: Int = 0
	var ladderPosition: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerId = stream.readVarLong().toDouble()
		playerName = stream.readUTF()
		breed = stream.readUnsignedByte().toInt()
		sex = stream.readBoolean()
		level = stream.readVarShort().toInt()
		leagueId = stream.readVarShort().toInt()
		totalLeaguePoints = stream.readVarShort().toInt()
		ladderPosition = stream.readInt().toInt()
	}
}
