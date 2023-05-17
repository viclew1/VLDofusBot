package fr.lewon.dofus.bot.sniffer.model.messages.game.finishmoves

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FinishMoveSetRequestMessage : NetworkMessage() {
	var finishMoveId: Int = 0
	var finishMoveState: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		finishMoveId = stream.readInt().toInt()
		finishMoveState = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 4215
}
