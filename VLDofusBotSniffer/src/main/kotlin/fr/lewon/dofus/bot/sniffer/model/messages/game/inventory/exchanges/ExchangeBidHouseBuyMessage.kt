package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeBidHouseBuyMessage : NetworkMessage() {
	var uid: Int = 0
	var qty: Int = 0
	var price: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		uid = stream.readVarInt().toInt()
		qty = stream.readVarInt().toInt()
		price = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 3869
}
