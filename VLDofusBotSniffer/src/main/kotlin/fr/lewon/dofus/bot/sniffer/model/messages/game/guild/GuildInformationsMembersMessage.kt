package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.guild.GuildMemberInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildInformationsMembersMessage : NetworkMessage() {
	var members: ArrayList<GuildMemberInfo> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		members = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GuildMemberInfo()
			item.deserialize(stream)
			members.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 5588
}
