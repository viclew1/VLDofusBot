package fr.lewon.dofus.bot.sniffer.model.messages.game.collector.tax

import fr.lewon.dofus.bot.sniffer.model.types.game.collector.tax.TaxCollectorOrderedSpell
import fr.lewon.dofus.bot.sniffer.model.types.game.uuid
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AddTaxCollectorPresetSpellMessage : NetworkMessage() {
	lateinit var presetId: uuid
	lateinit var spell: TaxCollectorOrderedSpell
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		presetId = uuid()
		presetId.deserialize(stream)
		spell = TaxCollectorOrderedSpell()
		spell.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 4511
}
