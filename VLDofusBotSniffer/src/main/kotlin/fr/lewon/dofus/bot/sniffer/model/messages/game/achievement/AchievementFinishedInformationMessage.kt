package fr.lewon.dofus.bot.sniffer.model.messages.game.achievement

import fr.lewon.dofus.bot.sniffer.model.types.game.achievement.AchievementAchievedRewardable
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AchievementFinishedInformationMessage : AchievementFinishedMessage() {
	var name: String = ""
	var playerId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		name = stream.readUTF()
		playerId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 7456
}
