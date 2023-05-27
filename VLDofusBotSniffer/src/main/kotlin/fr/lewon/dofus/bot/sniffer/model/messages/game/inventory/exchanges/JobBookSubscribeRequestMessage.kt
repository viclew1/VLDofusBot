package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobBookSubscribeRequestMessage : NetworkMessage() {
	var jobIds: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		jobIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUnsignedByte().toInt()
			jobIds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4734
}
