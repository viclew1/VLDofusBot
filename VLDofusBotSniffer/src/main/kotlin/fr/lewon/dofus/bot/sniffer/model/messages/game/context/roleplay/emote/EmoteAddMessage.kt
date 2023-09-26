package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.emote

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class EmoteAddMessage : NetworkMessage() {
	var emoteId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		emoteId = stream.readUnsignedShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 5327
}
