package fr.lewon.dofus.bot.sniffer.model.messages.game.collector.tax

import fr.lewon.dofus.bot.sniffer.model.types.game.collector.tax.TaxCollectorOrderedSpell
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorOrderedSpellUpdatedMessage : NetworkMessage() {
	var taxCollectorId: Double = 0.0
	var taxCollectorSpells: ArrayList<TaxCollectorOrderedSpell> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		taxCollectorId = stream.readDouble().toDouble()
		taxCollectorSpells = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = TaxCollectorOrderedSpell()
			item.deserialize(stream)
			taxCollectorSpells.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 7681
}
