package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeStartOkCraftWithInformationMessage : ExchangeStartOkCraftMessage() {
	var skillId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		skillId = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 187
}
