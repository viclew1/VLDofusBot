package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.breach

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.MonsterInGroupLightInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BreachBranch : NetworkType() {
	var room: Int = 0
	var element: Int = 0
	var bosses: ArrayList<MonsterInGroupLightInformations> = ArrayList()
	var map: Double = 0.0
	var score: Int = 0
	var relativeScore: Int = 0
	var monsters: ArrayList<MonsterInGroupLightInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		room = stream.readUnsignedByte().toInt()
		element = stream.readInt().toInt()
		bosses = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = MonsterInGroupLightInformations()
			item.deserialize(stream)
			bosses.add(item)
		}
		map = stream.readDouble().toDouble()
		score = stream.readUnsignedShort().toInt()
		relativeScore = stream.readUnsignedShort().toInt()
		monsters = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = MonsterInGroupLightInformations()
			item.deserialize(stream)
			monsters.add(item)
		}
	}
}
