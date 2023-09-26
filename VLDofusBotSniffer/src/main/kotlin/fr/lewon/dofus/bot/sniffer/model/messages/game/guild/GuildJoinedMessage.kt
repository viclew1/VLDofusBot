package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GuildInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildJoinedMessage : NetworkMessage() {
	lateinit var guildInfo: GuildInformations
	var rankId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guildInfo = GuildInformations()
		guildInfo.deserialize(stream)
		rankId = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 3312
}
