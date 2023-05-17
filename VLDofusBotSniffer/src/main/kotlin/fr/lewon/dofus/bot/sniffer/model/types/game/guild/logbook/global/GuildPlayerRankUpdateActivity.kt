package fr.lewon.dofus.bot.sniffer.model.types.game.guild.logbook.global

import fr.lewon.dofus.bot.sniffer.model.types.game.guild.logbook.GuildLogbookEntryBasicInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.rank.RankMinimalInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildPlayerRankUpdateActivity : GuildLogbookEntryBasicInformation() {
	lateinit var guildRankMinimalInfos: RankMinimalInformation
	var sourcePlayerId: Double = 0.0
	var targetPlayerId: Double = 0.0
	var sourcePlayerName: String = ""
	var targetPlayerName: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guildRankMinimalInfos = RankMinimalInformation()
		guildRankMinimalInfos.deserialize(stream)
		sourcePlayerId = stream.readVarLong().toDouble()
		targetPlayerId = stream.readVarLong().toDouble()
		sourcePlayerName = stream.readUTF()
		targetPlayerName = stream.readUTF()
	}
}
