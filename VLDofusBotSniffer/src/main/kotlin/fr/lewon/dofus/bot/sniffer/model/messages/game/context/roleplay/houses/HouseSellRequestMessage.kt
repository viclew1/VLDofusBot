package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.houses

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HouseSellRequestMessage : NetworkMessage() {
	var instanceId: Int = 0
	var amount: Double = 0.0
	var forSale: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		instanceId = stream.readInt().toInt()
		amount = stream.readVarLong().toDouble()
		forSale = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 1623
}
