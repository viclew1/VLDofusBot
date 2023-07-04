package fr.lewon.dofus.bot.sniffer.model.messages.secure

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TrustStatusMessage : NetworkMessage() {
	var certified: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		certified = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 9399
}
