package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.guild.GuildMemberInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildInformationsMemberUpdateMessage : NetworkMessage() {
	lateinit var member: GuildMemberInfo
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		member = GuildMemberInfo()
		member.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 8691
}
