package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeOkMultiCraftMessage : NetworkMessage() {
	var initiatorId: Double = 0.0
	var otherId: Double = 0.0
	var role: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		initiatorId = stream.readVarLong().toDouble()
		otherId = stream.readVarLong().toDouble()
		role = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 3063
}
