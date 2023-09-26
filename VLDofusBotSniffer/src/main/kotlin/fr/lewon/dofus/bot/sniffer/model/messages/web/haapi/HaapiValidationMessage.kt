package fr.lewon.dofus.bot.sniffer.model.messages.web.haapi

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HaapiValidationMessage : NetworkMessage() {
	var action: Int = 0
	var code: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		action = stream.readUnsignedByte().toInt()
		code = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 4420
}
