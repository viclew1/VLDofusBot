package fr.lewon.dofus.bot.sniffer.model.messages.game.actions

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionAcknowledgementMessage : NetworkMessage() {
	var valid: Boolean = false
	var actionId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		valid = stream.readBoolean()
		actionId = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 2057
}
