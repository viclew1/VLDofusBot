package fr.lewon.dofus.bot.sniffer.model.messages.game.collector.tax

import fr.lewon.dofus.bot.sniffer.model.types.game.collector.tax.TaxCollectorMovement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorMovementsOfflineMessage : NetworkMessage() {
	var movements: ArrayList<TaxCollectorMovement> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		movements = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = TaxCollectorMovement()
			item.deserialize(stream)
			movements.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 7893
}
