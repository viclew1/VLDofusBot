package fr.lewon.dofus.bot.sniffer.model.types.game.guild.logbook.chest

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItemNotInContainer
import fr.lewon.dofus.bot.sniffer.model.types.game.guild.logbook.GuildLogbookEntryBasicInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildLogbookChestActivity : GuildLogbookEntryBasicInformation() {
	var playerId: Double = 0.0
	var playerName: String = ""
	var eventType: Int = 0
	var quantity: Int = 0
	lateinit var obj: ObjectItemNotInContainer
	var sourceTabId: Int = 0
	var destinationTabId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerId = stream.readVarLong().toDouble()
		playerName = stream.readUTF()
		eventType = stream.readUnsignedByte().toInt()
		quantity = stream.readInt().toInt()
		obj = ObjectItemNotInContainer()
		obj.deserialize(stream)
		sourceTabId = stream.readInt().toInt()
		destinationTabId = stream.readInt().toInt()
	}
}
