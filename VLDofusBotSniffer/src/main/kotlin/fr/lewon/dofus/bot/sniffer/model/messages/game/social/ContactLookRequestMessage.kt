package fr.lewon.dofus.bot.sniffer.model.messages.game.social

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ContactLookRequestMessage : NetworkMessage() {
	var requestId: Int = 0
	var contactType: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		requestId = stream.readUnsignedByte().toInt()
		contactType = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 8471
}
