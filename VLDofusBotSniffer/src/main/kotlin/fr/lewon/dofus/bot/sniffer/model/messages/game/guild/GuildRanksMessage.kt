package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.rank.RankInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildRanksMessage : NetworkMessage() {
	var ranks: ArrayList<RankInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		ranks = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = RankInformation()
			item.deserialize(stream)
			ranks.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4603
}
