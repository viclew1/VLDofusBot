package fr.lewon.dofus.bot.sniffer.model.messages.debug

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DebugInClientMessage : NetworkMessage() {
	var level: Int = 0
	var message: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		level = stream.readUnsignedByte().toInt()
		message = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 968
}
