package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.challenge

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChallengeNumberMessage : NetworkMessage() {
	var challengeNumber: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		challengeNumber = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 2139
}
