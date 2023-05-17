package fr.lewon.dofus.bot.sniffer.model.messages.game.startup

import fr.lewon.dofus.bot.sniffer.model.types.game.startup.GameActionItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionItemListMessage : NetworkMessage() {
	var actions: ArrayList<GameActionItem> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		actions = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GameActionItem()
			item.deserialize(stream)
			actions.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9356
}
