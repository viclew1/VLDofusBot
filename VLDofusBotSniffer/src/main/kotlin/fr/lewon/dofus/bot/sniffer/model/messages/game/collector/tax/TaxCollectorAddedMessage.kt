package fr.lewon.dofus.bot.sniffer.model.messages.game.collector.tax

import fr.lewon.dofus.bot.sniffer.model.types.game.collector.tax.TaxCollectorInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorAddedMessage : NetworkMessage() {
	var callerId: Double = 0.0
	lateinit var description: TaxCollectorInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		callerId = stream.readVarLong().toDouble()
		description = ProtocolTypeManager.getInstance<TaxCollectorInformations>(stream.readUnsignedShort())
		description.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 624
}
