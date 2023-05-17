package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightResultPlayerListEntry : FightResultFighterListEntry() {
	var level: Int = 0
	var additional: ArrayList<FightResultAdditionalData> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		level = stream.readVarShort().toInt()
		additional = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<FightResultAdditionalData>(stream.readUnsignedShort())
			item.deserialize(stream)
			additional.add(item)
		}
	}
}
