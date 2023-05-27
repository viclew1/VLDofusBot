package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightTurnStartMessage : NetworkMessage() {
	var id: Double = 0.0
	var waitTime: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readDouble().toDouble()
		waitTime = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 4715
}
