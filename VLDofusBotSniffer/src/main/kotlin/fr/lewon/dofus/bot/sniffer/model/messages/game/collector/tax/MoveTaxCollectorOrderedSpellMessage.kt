package fr.lewon.dofus.bot.sniffer.model.messages.game.collector.tax

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MoveTaxCollectorOrderedSpellMessage : NetworkMessage() {
	var taxCollectorId: Double = 0.0
	var movedFrom: Int = 0
	var movedTo: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		taxCollectorId = stream.readDouble().toDouble()
		movedFrom = stream.readUnsignedByte().toInt()
		movedTo = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 5021
}
