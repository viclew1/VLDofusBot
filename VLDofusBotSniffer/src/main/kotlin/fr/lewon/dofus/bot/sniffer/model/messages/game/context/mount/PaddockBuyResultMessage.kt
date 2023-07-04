package fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockBuyResultMessage : NetworkMessage() {
	var paddockId: Double = 0.0
	var bought: Boolean = false
	var realPrice: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		paddockId = stream.readDouble().toDouble()
		bought = stream.readBoolean()
		realPrice = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 765
}
