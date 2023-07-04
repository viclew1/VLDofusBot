package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightStartingPositions
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MapFightStartPositionsUpdateMessage : NetworkMessage() {
	var mapId: Double = 0.0
	lateinit var fightStartPositions: FightStartingPositions
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		mapId = stream.readDouble().toDouble()
		fightStartPositions = FightStartingPositions()
		fightStartPositions.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 3445
}
