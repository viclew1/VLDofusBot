package fr.lewon.dofus.bot.sniffer.model.messages.game.achievement

import fr.lewon.dofus.bot.sniffer.model.types.game.achievement.Achievement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AchievementAlmostFinishedDetailedListMessage : NetworkMessage() {
	var almostFinishedAchievements: ArrayList<Achievement> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		almostFinishedAchievements = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = Achievement()
			item.deserialize(stream)
			almostFinishedAchievements.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 211
}
