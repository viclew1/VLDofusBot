package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.types.game.inventory.exchanges.RecycledItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class EvolutiveObjectRecycleResultMessage : NetworkMessage() {
	var recycledItems: ArrayList<RecycledItem> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		recycledItems = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = RecycledItem()
			item.deserialize(stream)
			recycledItems.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9667
}
