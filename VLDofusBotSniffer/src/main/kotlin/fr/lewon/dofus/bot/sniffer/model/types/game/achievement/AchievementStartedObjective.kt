package fr.lewon.dofus.bot.sniffer.model.types.game.achievement

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AchievementStartedObjective : AchievementObjective() {
	var value: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		value = stream.readVarShort().toInt()
	}
}
