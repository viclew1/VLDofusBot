package fr.lewon.dofus.bot.sniffer.model.messages.game.context.dungeon

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DungeonKeyRingMessage : NetworkMessage() {
	var availables: ArrayList<Int> = ArrayList()
	var unavailables: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		availables = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			availables.add(item)
		}
		unavailables = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			unavailables.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1249
}
