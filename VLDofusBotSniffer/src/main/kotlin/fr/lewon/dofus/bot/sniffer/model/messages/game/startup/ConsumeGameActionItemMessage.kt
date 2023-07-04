package fr.lewon.dofus.bot.sniffer.model.messages.game.startup

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ConsumeGameActionItemMessage : NetworkMessage() {
	var actionId: Int = 0
	var characterId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		actionId = stream.readInt().toInt()
		characterId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 2860
}
