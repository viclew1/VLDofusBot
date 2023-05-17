package fr.lewon.dofus.bot.sniffer.model.messages.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.game.friend.IgnoredInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IgnoredAddedMessage : NetworkMessage() {
	lateinit var ignoreAdded: IgnoredInformations
	var session: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		ignoreAdded = ProtocolTypeManager.getInstance<IgnoredInformations>(stream.readUnsignedShort())
		ignoreAdded.deserialize(stream)
		session = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 3303
}
