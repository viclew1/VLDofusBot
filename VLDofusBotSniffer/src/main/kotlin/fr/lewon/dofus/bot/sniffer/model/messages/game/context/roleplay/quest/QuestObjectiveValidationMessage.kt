package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.quest

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class QuestObjectiveValidationMessage : NetworkMessage() {
	var questId: Int = 0
	var objectiveId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		questId = stream.readVarShort().toInt()
		objectiveId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 9262
}
