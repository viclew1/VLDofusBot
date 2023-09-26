package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItemToSellInNpcShop
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeStartOkNpcShopMessage : NetworkMessage() {
	var npcSellerId: Double = 0.0
	var tokenId: Int = 0
	var objectsInfos: ArrayList<ObjectItemToSellInNpcShop> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		npcSellerId = stream.readDouble().toDouble()
		tokenId = stream.readVarInt().toInt()
		objectsInfos = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ObjectItemToSellInNpcShop()
			item.deserialize(stream)
			objectsInfos.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9513
}
