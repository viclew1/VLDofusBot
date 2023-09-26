package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangePlayerMultiCraftRequestMessage : ExchangeRequestMessage() {
	var target: Double = 0.0
	var skillId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		target = stream.readVarLong().toDouble()
		skillId = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 5318
}
