package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.breach

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BreachExitResponseMessage : NetworkMessage() {
	var exited: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		exited = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 6768
}
