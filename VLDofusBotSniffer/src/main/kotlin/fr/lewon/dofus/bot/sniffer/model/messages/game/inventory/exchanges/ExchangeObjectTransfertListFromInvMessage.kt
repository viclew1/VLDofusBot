package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeObjectTransfertListFromInvMessage : NetworkMessage() {
	var ids: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		ids = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			ids.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1197
}
