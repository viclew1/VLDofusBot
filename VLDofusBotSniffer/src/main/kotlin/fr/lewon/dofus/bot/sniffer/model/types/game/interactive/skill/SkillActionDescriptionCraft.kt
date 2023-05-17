package fr.lewon.dofus.bot.sniffer.model.types.game.interactive.skill

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SkillActionDescriptionCraft : SkillActionDescription() {
	var probability: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		probability = stream.readUnsignedByte().toInt()
	}
}
