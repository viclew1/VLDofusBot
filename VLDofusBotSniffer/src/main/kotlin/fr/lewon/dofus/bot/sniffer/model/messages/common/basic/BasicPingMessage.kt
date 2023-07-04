package fr.lewon.dofus.bot.sniffer.model.messages.common.basic

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BasicPingMessage : NetworkMessage() {
	var quiet: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		quiet = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 4681
}
