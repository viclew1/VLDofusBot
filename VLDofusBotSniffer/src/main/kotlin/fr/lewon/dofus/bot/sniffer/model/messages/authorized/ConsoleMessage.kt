package fr.lewon.dofus.bot.sniffer.model.messages.authorized

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ConsoleMessage : NetworkMessage() {
	var type: Int = 0
	var content: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		type = stream.readUnsignedByte().toInt()
		content = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 5631
}
