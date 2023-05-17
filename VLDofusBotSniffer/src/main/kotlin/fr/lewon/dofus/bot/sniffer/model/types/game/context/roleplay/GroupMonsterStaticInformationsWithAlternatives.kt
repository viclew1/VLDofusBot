package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GroupMonsterStaticInformationsWithAlternatives : GroupMonsterStaticInformations() {
	var alternatives: ArrayList<AlternativeMonstersInGroupLightInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		alternatives = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = AlternativeMonstersInGroupLightInformations()
			item.deserialize(stream)
			alternatives.add(item)
		}
	}
}
