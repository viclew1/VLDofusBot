package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.SellerBuyerDescriptor
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeStartedBidBuyerMessage : NetworkMessage() {
	lateinit var buyerDescriptor: SellerBuyerDescriptor
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		buyerDescriptor = SellerBuyerDescriptor()
		buyerDescriptor.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 69
}
