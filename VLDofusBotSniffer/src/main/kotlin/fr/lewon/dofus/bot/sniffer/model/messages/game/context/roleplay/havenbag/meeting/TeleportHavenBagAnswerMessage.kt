package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.havenbag.meeting

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TeleportHavenBagAnswerMessage : NetworkMessage() {
	var accept: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		accept = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 4689
}
