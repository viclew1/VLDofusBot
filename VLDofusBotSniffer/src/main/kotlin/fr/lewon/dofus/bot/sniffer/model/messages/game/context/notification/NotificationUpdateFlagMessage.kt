package fr.lewon.dofus.bot.sniffer.model.messages.game.context.notification

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class NotificationUpdateFlagMessage : NetworkMessage() {
	var index: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		index = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 2170
}
