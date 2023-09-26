package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.houses

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HouseBuyResultMessage : NetworkMessage() {
	var secondHand: Boolean = false
	var bought: Boolean = false
	var houseId: Int = 0
	var instanceId: Int = 0
	var realPrice: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		secondHand = BooleanByteWrapper.getFlag(_box0, 0)
		bought = BooleanByteWrapper.getFlag(_box0, 1)
		houseId = stream.readVarInt().toInt()
		instanceId = stream.readInt().toInt()
		realPrice = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 4028
}
