package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeStartOkMulticraftCustomerMessage : NetworkMessage() {
	var skillId: Int = 0
	var crafterJobLevel: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		skillId = stream.readVarInt().toInt()
		crafterJobLevel = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 128
}
