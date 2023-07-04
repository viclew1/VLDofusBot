package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.emote

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class EmotePlayMessage : EmotePlayAbstractMessage() {
	var actorId: Double = 0.0
	var accountId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		actorId = stream.readDouble().toDouble()
		accountId = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 3603
}
