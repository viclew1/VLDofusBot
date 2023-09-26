package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.GameFightCharacteristics
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class RefreshCharacterStatsMessage : NetworkMessage() {
	var fighterId: Double = 0.0
	lateinit var stats: GameFightCharacteristics
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fighterId = stream.readDouble().toDouble()
		stats = GameFightCharacteristics()
		stats.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 1113
}
