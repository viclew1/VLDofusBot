package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges.ExchangeObjectMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeObjectRemovedFromBagMessage : ExchangeObjectMessage() {
	var objectUID: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objectUID = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 5167
}
