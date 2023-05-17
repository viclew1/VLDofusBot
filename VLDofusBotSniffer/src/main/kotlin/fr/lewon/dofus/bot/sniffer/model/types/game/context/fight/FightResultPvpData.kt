package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightResultPvpData : FightResultAdditionalData() {
	var grade: Int = 0
	var minHonorForGrade: Int = 0
	var maxHonorForGrade: Int = 0
	var honor: Int = 0
	var honorDelta: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		grade = stream.readUnsignedByte().toInt()
		minHonorForGrade = stream.readVarShort().toInt()
		maxHonorForGrade = stream.readVarShort().toInt()
		honor = stream.readVarShort().toInt()
		honorDelta = stream.readVarShort().toInt()
	}
}
