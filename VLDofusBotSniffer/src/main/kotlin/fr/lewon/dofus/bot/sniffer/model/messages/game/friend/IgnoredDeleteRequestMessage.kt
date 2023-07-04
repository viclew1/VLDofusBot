package fr.lewon.dofus.bot.sniffer.model.messages.game.friend

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IgnoredDeleteRequestMessage : NetworkMessage() {
	var accountId: Int = 0
	var session: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		accountId = stream.readInt().toInt()
		session = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 9406
}
