package fr.lewon.dofus.bot.sniffer.model.messages.game.guild.application

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildUpdateApplicationMessage : NetworkMessage() {
	var applyText: String = ""
	var guildId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		applyText = stream.readUTF()
		guildId = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 1048
}
