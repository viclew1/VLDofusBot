package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightPlacementSwapPositionsCancelledMessage : NetworkMessage() {
	var requestId: Int = 0
	var cancellerId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		requestId = stream.readInt().toInt()
		cancellerId = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 7121
}
