package fr.lewon.dofus.bot.sniffer.model.types.game.paddock

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GuildInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockGuildedInformations : PaddockBuyableInformations() {
	var deserted: Boolean = false
	lateinit var guildInfo: GuildInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		deserted = stream.readBoolean()
		guildInfo = GuildInformations()
		guildInfo.deserialize(stream)
	}
}
