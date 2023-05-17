package fr.lewon.dofus.bot.sniffer.model.messages.game.idol

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IdolPartyRegisterRequestMessage : NetworkMessage() {
	var register: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		register = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 5724
}
