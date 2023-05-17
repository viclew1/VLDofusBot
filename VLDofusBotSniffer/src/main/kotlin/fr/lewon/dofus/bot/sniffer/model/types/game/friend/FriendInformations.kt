package fr.lewon.dofus.bot.sniffer.model.types.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FriendInformations : AbstractContactInformations() {
	var playerState: Int = 0
	var lastConnection: Int = 0
	var achievementPoints: Int = 0
	var leagueId: Int = 0
	var ladderPosition: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerState = stream.readUnsignedByte().toInt()
		lastConnection = stream.readVarShort().toInt()
		achievementPoints = stream.readInt().toInt()
		leagueId = stream.readVarShort().toInt()
		ladderPosition = stream.readInt().toInt()
	}
}
