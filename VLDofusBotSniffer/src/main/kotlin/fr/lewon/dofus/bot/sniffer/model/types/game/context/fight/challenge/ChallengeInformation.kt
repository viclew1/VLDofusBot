package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.challenge

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChallengeInformation : NetworkType() {
	var challengeId: Int = 0
	var targetsList: ArrayList<ChallengeTargetInformation> = ArrayList()
	var dropBonus: Int = 0
	var xpBonus: Int = 0
	var state: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		challengeId = stream.readVarInt().toInt()
		targetsList = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<ChallengeTargetInformation>(stream.readUnsignedShort())
			item.deserialize(stream)
			targetsList.add(item)
		}
		dropBonus = stream.readVarInt().toInt()
		xpBonus = stream.readVarInt().toInt()
		state = stream.readUnsignedByte().toInt()
	}
}
