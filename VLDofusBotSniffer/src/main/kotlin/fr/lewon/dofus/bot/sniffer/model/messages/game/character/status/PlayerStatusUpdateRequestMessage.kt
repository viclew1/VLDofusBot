package fr.lewon.dofus.bot.sniffer.model.messages.game.character.status

import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PlayerStatusUpdateRequestMessage : NetworkMessage() {
	lateinit var status: PlayerStatus
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		status = ProtocolTypeManager.getInstance<PlayerStatus>(stream.readUnsignedShort())
		status.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 1929
}
