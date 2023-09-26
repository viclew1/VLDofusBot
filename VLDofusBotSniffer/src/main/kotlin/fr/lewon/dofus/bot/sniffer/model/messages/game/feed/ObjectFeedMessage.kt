package fr.lewon.dofus.bot.sniffer.model.messages.game.feed

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItemQuantity
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ObjectFeedMessage : NetworkMessage() {
	var objectUID: Int = 0
	var meal: ArrayList<ObjectItemQuantity> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objectUID = stream.readVarInt().toInt()
		meal = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ObjectItemQuantity()
			item.deserialize(stream)
			meal.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 2738
}
