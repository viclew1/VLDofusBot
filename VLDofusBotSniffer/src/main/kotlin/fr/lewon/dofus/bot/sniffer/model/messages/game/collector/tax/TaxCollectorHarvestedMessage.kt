package fr.lewon.dofus.bot.sniffer.model.messages.game.collector.tax

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorHarvestedMessage : NetworkMessage() {
	var taxCollectorId: Double = 0.0
	var harvesterId: Double = 0.0
	var harvesterName: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		taxCollectorId = stream.readDouble().toDouble()
		harvesterId = stream.readVarLong().toDouble()
		harvesterName = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 9412
}
