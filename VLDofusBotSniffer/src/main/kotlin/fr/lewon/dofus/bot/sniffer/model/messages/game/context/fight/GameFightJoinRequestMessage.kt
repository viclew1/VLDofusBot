package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightJoinRequestMessage : NetworkMessage() {
	var fighterId: Double = 0.0
	var fightId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fighterId = stream.readDouble().toDouble()
		fightId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 6242
}
