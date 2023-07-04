package fr.lewon.dofus.bot.sniffer.model.messages.game.chat.channel

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChannelEnablingChangeMessage : NetworkMessage() {
	var channel: Int = 0
	var enable: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		channel = stream.readUnsignedByte().toInt()
		enable = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 5330
}
