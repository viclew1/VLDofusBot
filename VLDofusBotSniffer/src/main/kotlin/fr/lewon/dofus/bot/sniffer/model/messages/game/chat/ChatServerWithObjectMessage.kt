package fr.lewon.dofus.bot.sniffer.model.messages.game.chat

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChatServerWithObjectMessage : ChatServerMessage() {
	var objects: ArrayList<ObjectItem> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objects = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ObjectItem()
			item.deserialize(stream)
			objects.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 8618
}
