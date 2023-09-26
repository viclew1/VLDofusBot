package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.spells

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.SpellItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SpellListMessage : NetworkMessage() {
	var spellPrevisualization: Boolean = false
	var spells: ArrayList<SpellItem> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		spellPrevisualization = stream.readBoolean()
		spells = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = SpellItem()
			item.deserialize(stream)
			spells.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 7309
}
