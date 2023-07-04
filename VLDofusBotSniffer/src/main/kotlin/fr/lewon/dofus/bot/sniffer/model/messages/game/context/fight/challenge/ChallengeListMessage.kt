package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.challenge

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.challenge.ChallengeInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChallengeListMessage : NetworkMessage() {
	var challengesInformation: ArrayList<ChallengeInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		challengesInformation = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ChallengeInformation()
			item.deserialize(stream)
			challengesInformation.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 6466
}
