package fr.lewon.dofus.bot.sniffer.model.types.game.alliance

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class KohScore : NetworkType() {
	var avaScoreTypeEnum: Int = 0
	var roundScores: Int = 0
	var cumulScores: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		avaScoreTypeEnum = stream.readUnsignedByte().toInt()
		roundScores = stream.readInt().toInt()
		cumulScores = stream.readInt().toInt()
	}
}
