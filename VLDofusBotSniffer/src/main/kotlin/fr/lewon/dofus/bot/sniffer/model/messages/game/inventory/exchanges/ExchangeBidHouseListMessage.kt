package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeBidHouseListMessage : NetworkMessage() {
	var objectGID: Int = 0
	var follow: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objectGID = stream.readVarInt().toInt()
		follow = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 9324
}
