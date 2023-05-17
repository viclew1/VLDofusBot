package fr.lewon.dofus.bot.sniffer.model.types.game.paddock

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockInstancesInformations : PaddockInformations() {
	var paddocks: ArrayList<PaddockBuyableInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		paddocks = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<PaddockBuyableInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			paddocks.add(item)
		}
	}
}
