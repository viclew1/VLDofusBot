package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.havenbag

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class KickHavenBagRequestMessage : NetworkMessage() {
	var guestId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guestId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 5696
}
