package fr.lewon.dofus.bot.sniffer.model.messages.game.character.spell.forgettable

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ForgettableSpellItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ForgettableSpellListUpdateMessage : NetworkMessage() {
	var action: Int = 0
	var spells: ArrayList<ForgettableSpellItem> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		action = stream.readUnsignedByte().toInt()
		spells = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ForgettableSpellItem()
			item.deserialize(stream)
			spells.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 3361
}
