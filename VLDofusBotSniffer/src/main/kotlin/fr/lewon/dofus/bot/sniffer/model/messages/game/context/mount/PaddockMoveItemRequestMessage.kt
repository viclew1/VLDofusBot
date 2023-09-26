package fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockMoveItemRequestMessage : NetworkMessage() {
	var oldCellId: Int = 0
	var newCellId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		oldCellId = stream.readVarShort().toInt()
		newCellId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 6010
}
