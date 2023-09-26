package fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MountRidingMessage : NetworkMessage() {
	var isRiding: Boolean = false
	var isAutopilot: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		isRiding = BooleanByteWrapper.getFlag(_box0, 0)
		isAutopilot = BooleanByteWrapper.getFlag(_box0, 1)
	}
	override fun getNetworkMessageId(): Int = 6377
}
