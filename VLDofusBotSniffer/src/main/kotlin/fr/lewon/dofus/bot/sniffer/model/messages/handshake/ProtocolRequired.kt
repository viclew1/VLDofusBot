package fr.lewon.dofus.bot.sniffer.model.messages.handshake

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ProtocolRequired : NetworkMessage() {
	var version: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		version = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 610
}
