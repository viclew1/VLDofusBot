package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeBidHouseBuyResultMessage : NetworkMessage() {
	var uid: Int = 0
	var bought: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		uid = stream.readVarInt().toInt()
		bought = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 8142
}
