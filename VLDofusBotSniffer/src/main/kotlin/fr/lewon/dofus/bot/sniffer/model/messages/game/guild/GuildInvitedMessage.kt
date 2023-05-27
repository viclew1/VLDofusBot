package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GuildInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildInvitedMessage : NetworkMessage() {
	var recruterName: String = ""
	lateinit var guildInfo: GuildInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		recruterName = stream.readUTF()
		guildInfo = GuildInformations()
		guildInfo.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 4875
}
