package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeBidHouseInListRemovedMessage : NetworkMessage() {
	var itemUID: Int = 0
	var objectGID: Int = 0
	var objectType: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		itemUID = stream.readInt().toInt()
		objectGID = stream.readVarInt().toInt()
		objectType = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 8842
}
