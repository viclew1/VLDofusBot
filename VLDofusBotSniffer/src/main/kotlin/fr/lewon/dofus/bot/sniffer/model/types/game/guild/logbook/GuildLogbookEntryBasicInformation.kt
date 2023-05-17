package fr.lewon.dofus.bot.sniffer.model.types.game.guild.logbook

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildLogbookEntryBasicInformation : NetworkType() {
	var id: Int = 0
	var date: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readVarInt().toInt()
		date = stream.readDouble().toDouble()
	}
}
