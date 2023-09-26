package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory

import fr.lewon.dofus.bot.sniffer.model.types.game.inventory.StorageTabInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MultiTabStorageMessage : NetworkMessage() {
	var tabs: ArrayList<StorageTabInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		tabs = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = StorageTabInformation()
			item.deserialize(stream)
			tabs.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1638
}
