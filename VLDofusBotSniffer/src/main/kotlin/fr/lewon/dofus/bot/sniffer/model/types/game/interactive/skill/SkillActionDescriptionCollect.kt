package fr.lewon.dofus.bot.sniffer.model.types.game.interactive.skill

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SkillActionDescriptionCollect : SkillActionDescriptionTimed() {
	var min: Int = 0
	var max: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		min = stream.readVarShort().toInt()
		max = stream.readVarShort().toInt()
	}
}
