package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.quest

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class QuestActiveDetailedInformations : QuestActiveInformations() {
	var stepId: Int = 0
	var objectives: ArrayList<QuestObjectiveInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		stepId = stream.readVarShort().toInt()
		objectives = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<QuestObjectiveInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			objectives.add(item)
		}
	}
}
