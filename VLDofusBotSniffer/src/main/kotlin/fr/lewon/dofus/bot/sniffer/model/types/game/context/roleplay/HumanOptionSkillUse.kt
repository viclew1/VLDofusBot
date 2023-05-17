package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HumanOptionSkillUse : HumanOption() {
	var elementId: Int = 0
	var skillId: Int = 0
	var skillEndTime: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		elementId = stream.readVarInt().toInt()
		skillId = stream.readVarShort().toInt()
		skillEndTime = stream.readDouble().toDouble()
	}
}
