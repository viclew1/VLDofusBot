package fr.lewon.dofus.bot.sniffer.model.types.game.guild.logbook.global

import fr.lewon.dofus.bot.sniffer.model.types.game.context.MapCoordinatesExtended
import fr.lewon.dofus.bot.sniffer.model.types.game.guild.logbook.GuildLogbookEntryBasicInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildPaddockActivity : GuildLogbookEntryBasicInformation() {
	var playerId: Double = 0.0
	var playerName: String = ""
	lateinit var paddockCoordinates: MapCoordinatesExtended
	var farmId: Double = 0.0
	var paddockEventType: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerId = stream.readVarLong().toDouble()
		playerName = stream.readUTF()
		paddockCoordinates = MapCoordinatesExtended()
		paddockCoordinates.deserialize(stream)
		farmId = stream.readDouble().toDouble()
		paddockEventType = stream.readUnsignedByte().toInt()
	}
}
