package fr.lewon.dofus.bot.sniffer.model.messages.game

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaginationAnswerAbstractMessage : NetworkMessage() {
	var offset: Double = 0.0
	var count: Int = 0
	var total: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		offset = stream.readDouble().toDouble()
		count = stream.readInt().toInt()
		total = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 500
}
