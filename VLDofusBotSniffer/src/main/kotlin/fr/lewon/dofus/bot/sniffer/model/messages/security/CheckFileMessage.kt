package fr.lewon.dofus.bot.sniffer.model.messages.security

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CheckFileMessage : NetworkMessage() {
	var filenameHash: String = ""
	var type: Int = 0
	var value: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		filenameHash = stream.readUTF()
		type = stream.readUnsignedByte().toInt()
		value = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 9355
}
