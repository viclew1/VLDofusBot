package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive.meeting

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GroupTeleportPlayerCloseMessage : NetworkMessage() {
	var mapId: Double = 0.0
	var requesterId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		mapId = stream.readDouble().toDouble()
		requesterId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 3418
}
