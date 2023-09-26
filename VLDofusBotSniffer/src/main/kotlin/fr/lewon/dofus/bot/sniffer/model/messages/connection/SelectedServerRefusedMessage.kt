package fr.lewon.dofus.bot.sniffer.model.messages.connection

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SelectedServerRefusedMessage : NetworkMessage() {
	var serverId: Int = 0
	var error: Int = 0
	var serverStatus: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		serverId = stream.readVarShort().toInt()
		error = stream.readUnsignedByte().toInt()
		serverStatus = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 6476
}
