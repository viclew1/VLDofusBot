package fr.lewon.dofus.bot.sniffer.model.messages.game.subscriber

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SubscriptionZoneMessage : NetworkMessage() {
	var active: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		active = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 497
}
