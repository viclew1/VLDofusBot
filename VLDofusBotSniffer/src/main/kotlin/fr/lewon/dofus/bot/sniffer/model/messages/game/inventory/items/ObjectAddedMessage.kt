package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ObjectAddedMessage : NetworkMessage() {
	lateinit var obj: ObjectItem
	var origin: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		obj = ObjectItem()
		obj.deserialize(stream)
		origin = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 1715
}
