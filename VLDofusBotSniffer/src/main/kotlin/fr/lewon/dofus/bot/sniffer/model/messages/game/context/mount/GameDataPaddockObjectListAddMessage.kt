package fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount

import fr.lewon.dofus.bot.sniffer.model.types.game.paddock.PaddockItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameDataPaddockObjectListAddMessage : NetworkMessage() {
	var paddockItemDescription: ArrayList<PaddockItem> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		paddockItemDescription = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = PaddockItem()
			item.deserialize(stream)
			paddockItemDescription.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1254
}
