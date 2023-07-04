package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeStartOkRecycleTradeMessage : NetworkMessage() {
	var percentToPrism: Int = 0
	var percentToPlayer: Int = 0
	var adjacentSubareaPossessed: ArrayList<Int> = ArrayList()
	var adjacentSubareaUnpossessed: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		percentToPrism = stream.readUnsignedShort().toInt()
		percentToPlayer = stream.readUnsignedShort().toInt()
		adjacentSubareaPossessed = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			adjacentSubareaPossessed.add(item)
		}
		adjacentSubareaUnpossessed = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			adjacentSubareaUnpossessed.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 8048
}
