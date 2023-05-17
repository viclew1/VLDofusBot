package fr.lewon.dofus.bot.sniffer.model.types.game.presets

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ForgettableSpellsPreset : Preset() {
	lateinit var baseSpellsPreset: SpellsPreset
	var forgettableSpells: ArrayList<SpellForPreset> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		baseSpellsPreset = SpellsPreset()
		baseSpellsPreset.deserialize(stream)
		forgettableSpells = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = SpellForPreset()
			item.deserialize(stream)
			forgettableSpells.add(item)
		}
	}
}
