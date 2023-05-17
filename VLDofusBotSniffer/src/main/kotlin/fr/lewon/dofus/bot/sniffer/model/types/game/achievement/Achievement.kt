package fr.lewon.dofus.bot.sniffer.model.types.game.achievement

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class Achievement : NetworkType() {
	var id: Int = 0
	var finishedObjective: ArrayList<AchievementObjective> = ArrayList()
	var startedObjectives: ArrayList<AchievementStartedObjective> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readVarShort().toInt()
		finishedObjective = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = AchievementObjective()
			item.deserialize(stream)
			finishedObjective.add(item)
		}
		startedObjectives = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = AchievementStartedObjective()
			item.deserialize(stream)
			startedObjectives.add(item)
		}
	}
}
