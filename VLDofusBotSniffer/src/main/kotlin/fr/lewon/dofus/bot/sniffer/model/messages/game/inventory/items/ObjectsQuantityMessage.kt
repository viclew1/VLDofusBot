package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItemQuantity
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ObjectsQuantityMessage : NetworkMessage() {
	var objectsUIDAndQty: ArrayList<ObjectItemQuantity> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objectsUIDAndQty = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ObjectItemQuantity()
			item.deserialize(stream)
			objectsUIDAndQty.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 8051
}
