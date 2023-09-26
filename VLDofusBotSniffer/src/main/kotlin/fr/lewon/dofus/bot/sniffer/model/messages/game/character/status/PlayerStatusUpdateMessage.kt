package fr.lewon.dofus.bot.sniffer.model.messages.game.character.status

import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PlayerStatusUpdateMessage : NetworkMessage() {
	var accountId: Int = 0
	var playerId: Double = 0.0
	lateinit var status: PlayerStatus
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		accountId = stream.readInt().toInt()
		playerId = stream.readVarLong().toDouble()
		status = ProtocolTypeManager.getInstance<PlayerStatus>(stream.readUnsignedShort())
		status.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 4865
}
