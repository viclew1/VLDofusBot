package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.messages.game.PaginationAnswerAbstractMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.social.GuildFactSheetInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildSummaryMessage : PaginationAnswerAbstractMessage() {
	var guilds: ArrayList<GuildFactSheetInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guilds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GuildFactSheetInformations()
			item.deserialize(stream)
			guilds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9716
}
