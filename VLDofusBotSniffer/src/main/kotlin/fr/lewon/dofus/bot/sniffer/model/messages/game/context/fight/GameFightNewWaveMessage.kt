package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightNewWaveMessage : NetworkMessage() {
	var id: Int = 0
	var teamId: Int = 0
	var nbTurnBeforeNextWave: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readUnsignedByte().toInt()
		teamId = stream.readUnsignedByte().toInt()
		nbTurnBeforeNextWave = stream.readUnsignedShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 2674
}
