package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.BidExchangerObjectInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeTypesItemsExchangerDescriptionForUserMessage : NetworkMessage() {
	var objectGID: Int = 0
	var objectType: Int = 0
	var itemTypeDescriptions: ArrayList<BidExchangerObjectInfo> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objectGID = stream.readVarInt().toInt()
		objectType = stream.readInt().toInt()
		itemTypeDescriptions = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = BidExchangerObjectInfo()
			item.deserialize(stream)
			itemTypeDescriptions.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 177
}
