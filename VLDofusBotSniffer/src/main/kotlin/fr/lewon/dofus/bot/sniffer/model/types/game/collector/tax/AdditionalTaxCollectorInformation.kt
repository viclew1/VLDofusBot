package fr.lewon.dofus.bot.sniffer.model.types.game.collector.tax

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AdditionalTaxCollectorInformation : NetworkType() {
	var collectorCallerId: Double = 0.0
	var collectorCallerName: String = ""
	var date: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		collectorCallerId = stream.readVarLong().toDouble()
		collectorCallerName = stream.readUTF()
		date = stream.readInt().toInt()
	}
}
