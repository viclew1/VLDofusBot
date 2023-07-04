package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItemToSellInBid
import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.SellerBuyerDescriptor
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeStartedBidSellerMessage : NetworkMessage() {
	lateinit var sellerDescriptor: SellerBuyerDescriptor
	var objectsInfos: ArrayList<ObjectItemToSellInBid> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		sellerDescriptor = SellerBuyerDescriptor()
		sellerDescriptor.deserialize(stream)
		objectsInfos = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ObjectItemToSellInBid()
			item.deserialize(stream)
			objectsInfos.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 6402
}
