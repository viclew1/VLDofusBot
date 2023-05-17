package fr.lewon.dofus.bot.sniffer.model.types.game.house

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GuildInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HouseGuildedInformations : HouseInstanceInformations() {
	lateinit var guildInfo: GuildInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guildInfo = GuildInformations()
		guildInfo.deserialize(stream)
	}
}
