package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job

import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.skill.SkillActionDescription
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobDescription : NetworkType() {
	var jobId: Int = 0
	var skills: ArrayList<SkillActionDescription> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		jobId = stream.readUnsignedByte().toInt()
		skills = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<SkillActionDescription>(stream.readUnsignedShort())
			item.deserialize(stream)
			skills.add(item)
		}
	}
}
