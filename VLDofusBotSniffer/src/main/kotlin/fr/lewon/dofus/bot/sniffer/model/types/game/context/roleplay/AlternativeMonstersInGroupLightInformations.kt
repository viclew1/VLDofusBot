package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AlternativeMonstersInGroupLightInformations : NetworkType() {
	var playerCount: Int = 0
	var monsters: ArrayList<MonsterInGroupLightInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerCount = stream.readInt().toInt()
		monsters = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = MonsterInGroupLightInformations()
			item.deserialize(stream)
			monsters.add(item)
		}
	}
}
