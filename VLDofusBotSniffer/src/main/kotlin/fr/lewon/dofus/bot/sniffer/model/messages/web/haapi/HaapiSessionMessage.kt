package fr.lewon.dofus.bot.sniffer.model.messages.web.haapi

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HaapiSessionMessage : NetworkMessage() {
	var key: String = ""
	var type: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		key = stream.readUTF()
		type = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 4839
}
