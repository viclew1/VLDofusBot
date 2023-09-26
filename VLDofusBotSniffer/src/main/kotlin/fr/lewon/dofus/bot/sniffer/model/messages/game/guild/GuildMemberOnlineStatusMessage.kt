package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildMemberOnlineStatusMessage : NetworkMessage() {
	var memberId: Double = 0.0
	var online: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		memberId = stream.readVarLong().toDouble()
		online = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 5406
}
