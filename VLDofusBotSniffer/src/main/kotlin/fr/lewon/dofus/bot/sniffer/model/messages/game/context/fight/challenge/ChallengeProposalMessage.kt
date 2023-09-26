package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.challenge

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.challenge.ChallengeInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChallengeProposalMessage : NetworkMessage() {
	var challengeProposals: ArrayList<ChallengeInformation> = ArrayList()
	var timer: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		challengeProposals = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ChallengeInformation()
			item.deserialize(stream)
			challengeProposals.add(item)
		}
		timer = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 833
}
