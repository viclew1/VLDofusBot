package fr.lewon.dofus.bot.sniffer.model.types.game.achievement

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AchievementAchieved : NetworkType() {
	var id: Int = 0
	var achievedBy: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readVarShort().toInt()
		achievedBy = stream.readVarLong().toDouble()
	}
}
