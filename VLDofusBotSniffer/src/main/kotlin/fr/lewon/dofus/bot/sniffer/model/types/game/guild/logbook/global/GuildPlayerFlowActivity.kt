package fr.lewon.dofus.bot.sniffer.model.types.game.guild.logbook.global

import fr.lewon.dofus.bot.sniffer.model.types.game.guild.logbook.GuildLogbookEntryBasicInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildPlayerFlowActivity : GuildLogbookEntryBasicInformation() {
	var playerId: Double = 0.0
	var playerName: String = ""
	var playerFlowEventType: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerId = stream.readVarLong().toDouble()
		playerName = stream.readUTF()
		playerFlowEventType = stream.readUnsignedByte().toInt()
	}
}
