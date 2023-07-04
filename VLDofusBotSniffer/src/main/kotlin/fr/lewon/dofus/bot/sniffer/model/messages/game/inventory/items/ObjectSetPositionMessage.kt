package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ObjectSetPositionMessage : NetworkMessage() {
	var objectUID: Int = 0
	var position: Int = 0
	var quantity: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objectUID = stream.readVarInt().toInt()
		position = stream.readUnsignedShort().toInt()
		quantity = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 5462
}
