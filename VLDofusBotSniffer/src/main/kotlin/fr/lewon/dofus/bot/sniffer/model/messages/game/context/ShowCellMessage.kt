package fr.lewon.dofus.bot.sniffer.model.messages.game.context

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ShowCellMessage : NetworkMessage() {
	var sourceId: Double = 0.0
	var cellId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		sourceId = stream.readDouble().toDouble()
		cellId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 1929
}
