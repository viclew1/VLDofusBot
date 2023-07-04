package fr.lewon.dofus.bot.sniffer.model.messages.game.actions.sequence

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SequenceEndMessage : NetworkMessage() {
	var actionId: Int = 0
	var authorId: Double = 0.0
	var sequenceType: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		actionId = stream.readVarShort().toInt()
		authorId = stream.readDouble().toDouble()
		sequenceType = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 6647
}
