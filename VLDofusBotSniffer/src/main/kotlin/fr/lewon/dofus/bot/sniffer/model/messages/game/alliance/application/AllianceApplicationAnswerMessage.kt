package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance.application

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceApplicationAnswerMessage : NetworkMessage() {
	var accepted: Boolean = false
	var playerId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		accepted = stream.readBoolean()
		playerId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 8759
}
