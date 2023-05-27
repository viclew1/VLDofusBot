package fr.lewon.dofus.bot.sniffer.model.messages.game.house

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HouseTeleportRequestMessage : NetworkMessage() {
	var houseId: Int = 0
	var houseInstanceId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		houseId = stream.readVarInt().toInt()
		houseInstanceId = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 6456
}
