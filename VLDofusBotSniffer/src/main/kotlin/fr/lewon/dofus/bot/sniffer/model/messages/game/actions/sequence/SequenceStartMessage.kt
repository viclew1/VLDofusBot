package fr.lewon.dofus.bot.sniffer.model.messages.game.actions.sequence

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SequenceStartMessage : NetworkMessage() {
	var sequenceType: Int = 0
	var authorId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		sequenceType = stream.readUnsignedByte().toInt()
		authorId = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 7930
}
