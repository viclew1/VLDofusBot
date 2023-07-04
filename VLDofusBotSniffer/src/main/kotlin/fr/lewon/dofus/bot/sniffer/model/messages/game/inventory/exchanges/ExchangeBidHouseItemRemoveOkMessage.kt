package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeBidHouseItemRemoveOkMessage : NetworkMessage() {
	var sellerId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		sellerId = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 7081
}
