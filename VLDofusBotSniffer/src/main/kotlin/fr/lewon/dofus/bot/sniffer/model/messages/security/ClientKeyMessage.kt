package fr.lewon.dofus.bot.sniffer.model.messages.security

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ClientKeyMessage : NetworkMessage() {
	var key: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		key = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 3919
}
