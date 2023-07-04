package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeRequestMessage : NetworkMessage() {
	var exchangeType: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		exchangeType = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 2858
}
