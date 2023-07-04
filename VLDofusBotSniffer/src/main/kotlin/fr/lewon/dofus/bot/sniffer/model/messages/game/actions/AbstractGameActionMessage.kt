package fr.lewon.dofus.bot.sniffer.model.messages.game.actions

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AbstractGameActionMessage : NetworkMessage() {
	var actionId: Int = 0
	var sourceId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		actionId = stream.readVarShort().toInt()
		sourceId = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 409
}
