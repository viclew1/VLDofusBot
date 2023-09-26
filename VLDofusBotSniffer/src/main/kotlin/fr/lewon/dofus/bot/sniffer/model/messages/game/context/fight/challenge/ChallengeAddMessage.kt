package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.challenge

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.challenge.ChallengeInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChallengeAddMessage : NetworkMessage() {
	lateinit var challengeInformation: ChallengeInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		challengeInformation = ChallengeInformation()
		challengeInformation.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 838
}
