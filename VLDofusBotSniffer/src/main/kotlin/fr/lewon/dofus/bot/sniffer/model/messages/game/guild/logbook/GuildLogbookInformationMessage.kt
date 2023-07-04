package fr.lewon.dofus.bot.sniffer.model.messages.game.guild.logbook

import fr.lewon.dofus.bot.sniffer.model.types.game.guild.logbook.GuildLogbookEntryBasicInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildLogbookInformationMessage : NetworkMessage() {
	var globalActivities: ArrayList<GuildLogbookEntryBasicInformation> = ArrayList()
	var chestActivities: ArrayList<GuildLogbookEntryBasicInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		globalActivities = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<GuildLogbookEntryBasicInformation>(stream.readUnsignedShort())
			item.deserialize(stream)
			globalActivities.add(item)
		}
		chestActivities = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<GuildLogbookEntryBasicInformation>(stream.readUnsignedShort())
			item.deserialize(stream)
			chestActivities.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9456
}
