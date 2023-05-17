package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightPlacementSwapPositionsOfferMessage : NetworkMessage() {
	var requestId: Int = 0
	var requesterId: Double = 0.0
	var requesterCellId: Int = 0
	var requestedId: Double = 0.0
	var requestedCellId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		requestId = stream.readInt().toInt()
		requesterId = stream.readDouble().toDouble()
		requesterCellId = stream.readVarShort().toInt()
		requestedId = stream.readDouble().toDouble()
		requestedCellId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 7939
}
