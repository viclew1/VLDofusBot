package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.breach

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BreachTeleportResponseMessage : NetworkMessage() {
	var teleported: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		teleported = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 6396
}
