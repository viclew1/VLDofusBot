package fr.lewon.dofus.bot.sniffer.model.messages.game.achievement

import fr.lewon.dofus.bot.sniffer.model.types.game.achievement.AchievementAchievedRewardable
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AchievementFinishedMessage : NetworkMessage() {
	lateinit var achievement: AchievementAchievedRewardable
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		achievement = AchievementAchievedRewardable()
		achievement.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 9364
}
