package fr.lewon.dofus.bot.sniffer.model.messages.game.chat

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChatServerMessage : ChatAbstractServerMessage() {
	var senderId: Double = 0.0
	var senderName: String = ""
	var prefix: String = ""
	var senderAccountId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		senderId = stream.readDouble().toDouble()
		senderName = stream.readUTF()
		prefix = stream.readUTF()
		senderAccountId = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 358
}
