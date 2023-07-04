package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.alignment.war.effort

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.alignment.war.effort.AlignmentWarEffortInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AlignmentWarEffortProgressionMessage : NetworkMessage() {
	var effortProgressions: ArrayList<AlignmentWarEffortInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		effortProgressions = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = AlignmentWarEffortInformation()
			item.deserialize(stream)
			effortProgressions.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 8197
}
