package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class LivingObjectDissociateMessage : NetworkMessage() {
	var livingUID: Int = 0
	var livingPosition: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		livingUID = stream.readVarInt().toInt()
		livingPosition = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 2542
}
