package fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockSellRequestMessage : NetworkMessage() {
	var price: Double = 0.0
	var forSale: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		price = stream.readVarLong().toDouble()
		forSale = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 1043
}
