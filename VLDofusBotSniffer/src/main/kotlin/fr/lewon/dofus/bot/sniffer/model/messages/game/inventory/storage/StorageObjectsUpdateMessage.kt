package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.storage

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class StorageObjectsUpdateMessage : NetworkMessage() {
	var objectList: ArrayList<ObjectItem> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objectList = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ObjectItem()
			item.deserialize(stream)
			objectList.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4497
}
