package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.paddock

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockToSellFilterMessage : NetworkMessage() {
	var areaId: Int = 0
	var atLeastNbMount: Int = 0
	var atLeastNbMachine: Int = 0
	var maxPrice: Double = 0.0
	var orderBy: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		areaId = stream.readInt().toInt()
		atLeastNbMount = stream.readUnsignedByte().toInt()
		atLeastNbMachine = stream.readUnsignedByte().toInt()
		maxPrice = stream.readVarLong().toDouble()
		orderBy = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 1854
}
