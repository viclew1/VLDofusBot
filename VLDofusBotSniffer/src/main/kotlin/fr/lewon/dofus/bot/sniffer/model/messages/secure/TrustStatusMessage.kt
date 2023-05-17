package fr.lewon.dofus.bot.sniffer.model.messages.secure

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TrustStatusMessage : NetworkMessage() {
	var trusted: Boolean = false
	var certified: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		trusted = BooleanByteWrapper.getFlag(_box0, 0)
		certified = BooleanByteWrapper.getFlag(_box0, 1)
	}
	override fun getNetworkMessageId(): Int = 2199
}
