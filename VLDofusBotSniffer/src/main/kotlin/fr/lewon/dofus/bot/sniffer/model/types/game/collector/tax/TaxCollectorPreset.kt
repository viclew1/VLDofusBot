package fr.lewon.dofus.bot.sniffer.model.types.game.collector.tax

import fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic.CharacterCharacteristics
import fr.lewon.dofus.bot.sniffer.model.types.game.uuid
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorPreset : NetworkType() {
	lateinit var presetId: uuid
	var spells: ArrayList<TaxCollectorOrderedSpell> = ArrayList()
	lateinit var characteristics: CharacterCharacteristics
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		presetId = uuid()
		presetId.deserialize(stream)
		spells = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = TaxCollectorOrderedSpell()
			item.deserialize(stream)
			spells.add(item)
		}
		characteristics = CharacterCharacteristics()
		characteristics.deserialize(stream)
	}
}
