package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ArenaRanking : NetworkType() {
	var rank: Int = 0
	var bestRank: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		rank = stream.readVarShort().toInt()
		bestRank = stream.readVarShort().toInt()
	}
}
