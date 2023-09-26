package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItemToSellInBid
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeBidHouseItemAddOkMessage : NetworkMessage() {
	lateinit var itemInfo: ObjectItemToSellInBid
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		itemInfo = ObjectItemToSellInBid()
		itemInfo.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 7397
}
