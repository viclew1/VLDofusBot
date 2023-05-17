package fr.lewon.dofus.bot.sniffer.model.messages.game.achievement

import fr.lewon.dofus.bot.sniffer.model.types.game.achievement.Achievement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AchievementDetailedListMessage : NetworkMessage() {
	var startedAchievements: ArrayList<Achievement> = ArrayList()
	var finishedAchievements: ArrayList<Achievement> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		startedAchievements = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = Achievement()
			item.deserialize(stream)
			startedAchievements.add(item)
		}
		finishedAchievements = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = Achievement()
			item.deserialize(stream)
			finishedAchievements.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 3252
}
