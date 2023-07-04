package fr.lewon.dofus.bot.sniffer.model.messages.game.chat

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChatAbstractServerMessage : NetworkMessage() {
	var channel: Int = 0
	var content: String = ""
	var timestamp: Int = 0
	var fingerprint: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		channel = stream.readUnsignedByte().toInt()
		content = stream.readUTF()
		timestamp = stream.readInt().toInt()
		fingerprint = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 2850
}
