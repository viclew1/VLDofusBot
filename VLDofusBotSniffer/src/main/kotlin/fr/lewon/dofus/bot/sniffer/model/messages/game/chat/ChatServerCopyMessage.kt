package fr.lewon.dofus.bot.sniffer.model.messages.game.chat

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChatServerCopyMessage : ChatAbstractServerMessage() {
	var receiverId: Double = 0.0
	var receiverName: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		receiverId = stream.readVarLong().toDouble()
		receiverName = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 7495
}
