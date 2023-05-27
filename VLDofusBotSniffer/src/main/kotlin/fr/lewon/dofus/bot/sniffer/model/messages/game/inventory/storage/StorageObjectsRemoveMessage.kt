package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.storage

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class StorageObjectsRemoveMessage : NetworkMessage() {
	var objectUIDList: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objectUIDList = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			objectUIDList.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 5486
}
