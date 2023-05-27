package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightLoot : NetworkType() {
	var objects: ArrayList<FightLootObject> = ArrayList()
	var kamas: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objects = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = FightLootObject()
			item.deserialize(stream)
			objects.add(item)
		}
		kamas = stream.readVarLong().toDouble()
	}
}
