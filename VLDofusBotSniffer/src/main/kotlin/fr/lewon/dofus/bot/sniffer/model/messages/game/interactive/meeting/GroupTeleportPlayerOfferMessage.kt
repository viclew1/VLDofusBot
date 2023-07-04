package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive.meeting

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GroupTeleportPlayerOfferMessage : NetworkMessage() {
	var mapId: Double = 0.0
	var worldX: Int = 0
	var worldY: Int = 0
	var timeLeft: Int = 0
	var requesterId: Double = 0.0
	var requesterName: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		mapId = stream.readDouble().toDouble()
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		timeLeft = stream.readVarInt().toInt()
		requesterId = stream.readVarLong().toDouble()
		requesterName = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 9570
}
