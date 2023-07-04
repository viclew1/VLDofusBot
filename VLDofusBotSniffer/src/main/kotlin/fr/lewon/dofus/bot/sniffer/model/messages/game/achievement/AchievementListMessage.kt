package fr.lewon.dofus.bot.sniffer.model.messages.game.achievement

import fr.lewon.dofus.bot.sniffer.model.types.game.achievement.AchievementAchieved
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AchievementListMessage : NetworkMessage() {
	var finishedAchievements: ArrayList<AchievementAchieved> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		finishedAchievements = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<AchievementAchieved>(stream.readUnsignedShort())
			item.deserialize(stream)
			finishedAchievements.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4298
}
