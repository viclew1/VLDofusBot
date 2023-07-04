package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class InteractiveUseRequestMessage : NetworkMessage() {
	var elemId: Int = 0
	var skillInstanceUid: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		elemId = stream.readVarInt().toInt()
		skillInstanceUid = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 6295
}
