package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class InteractiveUseEndedMessage : NetworkMessage() {
	var elemId: Int = 0
	var skillId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		elemId = stream.readVarInt().toInt()
		skillId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 5459
}
