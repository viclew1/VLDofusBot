package fr.lewon.dofus.bot.sniffer.model.messages.game.achievement

import fr.lewon.dofus.bot.sniffer.model.types.game.achievement.Achievement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AchievementDetailsMessage : NetworkMessage() {
	lateinit var achievement: Achievement
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		achievement = Achievement()
		achievement.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 2146
}
