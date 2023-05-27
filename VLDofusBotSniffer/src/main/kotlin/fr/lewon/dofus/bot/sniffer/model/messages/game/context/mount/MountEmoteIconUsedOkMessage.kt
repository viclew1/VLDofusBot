package fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MountEmoteIconUsedOkMessage : NetworkMessage() {
	var mountId: Int = 0
	var reactionType: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		mountId = stream.readVarInt().toInt()
		reactionType = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 6868
}
