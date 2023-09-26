package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive.zaap

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TeleportRequestMessage : NetworkMessage() {
	var sourceType: Int = 0
	var destinationType: Int = 0
	var mapId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		sourceType = stream.readUnsignedByte().toInt()
		destinationType = stream.readUnsignedByte().toInt()
		mapId = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 6400
}
