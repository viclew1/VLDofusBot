package fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MountFeedRequestMessage : NetworkMessage() {
	var mountUid: Int = 0
	var mountLocation: Int = 0
	var mountFoodUid: Int = 0
	var quantity: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		mountUid = stream.readVarInt().toInt()
		mountLocation = stream.readUnsignedByte().toInt()
		mountFoodUid = stream.readVarInt().toInt()
		quantity = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 8008
}
