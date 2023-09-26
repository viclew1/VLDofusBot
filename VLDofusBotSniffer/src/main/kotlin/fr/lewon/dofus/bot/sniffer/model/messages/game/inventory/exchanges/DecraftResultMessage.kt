package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job.DecraftedItemStackInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DecraftResultMessage : NetworkMessage() {
	var results: ArrayList<DecraftedItemStackInfo> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		results = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = DecraftedItemStackInfo()
			item.deserialize(stream)
			results.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 2274
}
