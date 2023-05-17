package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.challenge

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChallengeResultMessage : NetworkMessage() {
	var challengeId: Int = 0
	var success: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		challengeId = stream.readVarShort().toInt()
		success = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 6642
}
