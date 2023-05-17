package fr.lewon.dofus.bot.sniffer.model.types.game.guild.logbook.global

import fr.lewon.dofus.bot.sniffer.model.types.game.guild.logbook.GuildLogbookEntryBasicInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.rank.RankMinimalInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildRankActivity : GuildLogbookEntryBasicInformation() {
	var rankActivityType: Int = 0
	lateinit var guildRankMinimalInfos: RankMinimalInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		rankActivityType = stream.readUnsignedByte().toInt()
		guildRankMinimalInfos = RankMinimalInformation()
		guildRankMinimalInfos.deserialize(stream)
	}
}
