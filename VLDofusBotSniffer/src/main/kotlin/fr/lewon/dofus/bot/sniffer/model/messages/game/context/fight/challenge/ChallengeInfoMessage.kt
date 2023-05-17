package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.challenge

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChallengeInfoMessage : NetworkMessage() {
	var challengeId: Int = 0
	var targetId: Double = 0.0
	var xpBonus: Int = 0
	var dropBonus: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		challengeId = stream.readVarShort().toInt()
		targetId = stream.readDouble().toDouble()
		xpBonus = stream.readVarInt().toInt()
		dropBonus = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 8147
}
