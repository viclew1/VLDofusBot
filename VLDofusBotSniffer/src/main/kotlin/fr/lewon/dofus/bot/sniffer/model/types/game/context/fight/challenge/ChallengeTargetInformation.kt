package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.challenge

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChallengeTargetInformation : NetworkType() {
	var targetId: Double = 0.0
	var targetCell: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		targetId = stream.readDouble().toDouble()
		targetCell = stream.readUnsignedShort().toInt()
	}
}
