package fr.lewon.dofus.bot.sniffer.model.messages.game.character.spell.forgettable

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ForgettableSpellDeleteMessage : NetworkMessage() {
	var reason: Int = 0
	var spells: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		reason = stream.readUnsignedByte().toInt()
		spells = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			spells.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 3198
}
