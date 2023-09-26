package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildChangeMemberParametersMessage : NetworkMessage() {
	var memberId: Double = 0.0
	var rankId: Int = 0
	var experienceGivenPercent: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		memberId = stream.readVarLong().toDouble()
		rankId = stream.readVarInt().toInt()
		experienceGivenPercent = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 4067
}
