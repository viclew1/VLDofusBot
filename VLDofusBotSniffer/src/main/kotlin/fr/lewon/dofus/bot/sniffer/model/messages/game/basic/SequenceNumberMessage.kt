package fr.lewon.dofus.bot.sniffer.model.messages.game.basic

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SequenceNumberMessage : NetworkMessage() {
	var number: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		number = stream.readUnsignedShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 278
}
