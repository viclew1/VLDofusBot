package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeStartedWithPodsMessage : ExchangeStartedMessage() {
	var firstCharacterId: Double = 0.0
	var firstCharacterCurrentWeight: Int = 0
	var firstCharacterMaxWeight: Int = 0
	var secondCharacterId: Double = 0.0
	var secondCharacterCurrentWeight: Int = 0
	var secondCharacterMaxWeight: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		firstCharacterId = stream.readDouble().toDouble()
		firstCharacterCurrentWeight = stream.readVarInt().toInt()
		firstCharacterMaxWeight = stream.readVarInt().toInt()
		secondCharacterId = stream.readDouble().toDouble()
		secondCharacterCurrentWeight = stream.readVarInt().toInt()
		secondCharacterMaxWeight = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 7482
}
