package fr.lewon.dofus.bot.sniffer.model.messages.game.chat

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChatClientMultiMessage : ChatAbstractClientMessage() {
	var channel: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		channel = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 7429
}
