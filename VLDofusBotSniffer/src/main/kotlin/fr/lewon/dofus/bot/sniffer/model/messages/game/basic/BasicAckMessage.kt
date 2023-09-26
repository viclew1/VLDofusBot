package fr.lewon.dofus.bot.sniffer.model.messages.game.basic

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BasicAckMessage : NetworkMessage() {
	var seq: Int = 0
	var lastPacketId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		seq = stream.readVarInt().toInt()
		lastPacketId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 9898
}
