package fr.lewon.dofus.bot.sniffer.model.messages.game.character.spell.forgettable

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ForgettableSpellClientActionMessage : NetworkMessage() {
	var spellId: Int = 0
	var action: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		spellId = stream.readInt().toInt()
		action = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 3179
}
