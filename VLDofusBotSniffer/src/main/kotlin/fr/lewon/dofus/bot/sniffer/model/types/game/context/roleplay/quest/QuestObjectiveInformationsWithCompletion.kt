package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.quest

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class QuestObjectiveInformationsWithCompletion : QuestObjectiveInformations() {
	var curCompletion: Int = 0
	var maxCompletion: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		curCompletion = stream.readVarShort().toInt()
		maxCompletion = stream.readVarShort().toInt()
	}
}
