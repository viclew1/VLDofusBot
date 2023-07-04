package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive.meeting

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TeleportPlayerAnswerMessage : NetworkMessage() {
	var accept: Boolean = false
	var requesterId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		accept = stream.readBoolean()
		requesterId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 8212
}
