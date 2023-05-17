package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.paddock

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockSellBuyDialogMessage : NetworkMessage() {
	var bsell: Boolean = false
	var ownerId: Int = 0
	var price: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		bsell = stream.readBoolean()
		ownerId = stream.readVarInt().toInt()
		price = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 6148
}
