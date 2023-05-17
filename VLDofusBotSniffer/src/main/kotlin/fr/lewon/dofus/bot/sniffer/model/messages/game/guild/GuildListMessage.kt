package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GuildInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildListMessage : NetworkMessage() {
	var guilds: ArrayList<GuildInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guilds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GuildInformations()
			item.deserialize(stream)
			guilds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 6219
}
