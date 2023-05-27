package fr.lewon.dofus.bot.sniffer.model.messages.game.chat.smiley

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MoodSmileyUpdateMessage : NetworkMessage() {
	var accountId: Int = 0
	var playerId: Double = 0.0
	var smileyId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		accountId = stream.readInt().toInt()
		playerId = stream.readVarLong().toDouble()
		smileyId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 8630
}
