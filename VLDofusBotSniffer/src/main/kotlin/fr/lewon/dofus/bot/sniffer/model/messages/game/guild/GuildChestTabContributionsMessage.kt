package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.guild.Contribution
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildChestTabContributionsMessage : NetworkMessage() {
	var contributions: ArrayList<Contribution> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		contributions = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = Contribution()
			item.deserialize(stream)
			contributions.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 3046
}
