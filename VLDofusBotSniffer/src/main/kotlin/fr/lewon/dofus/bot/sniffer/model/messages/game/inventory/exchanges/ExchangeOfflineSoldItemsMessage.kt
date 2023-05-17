package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItemQuantityPriceDateEffects
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeOfflineSoldItemsMessage : NetworkMessage() {
	var bidHouseItems: ArrayList<ObjectItemQuantityPriceDateEffects> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		bidHouseItems = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ObjectItemQuantityPriceDateEffects()
			item.deserialize(stream)
			bidHouseItems.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9314
}
