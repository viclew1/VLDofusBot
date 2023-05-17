package fr.lewon.dofus.bot.sniffer.model.messages.game.character.spell.forgettable

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ForgettableSpellEquipmentSlotsMessage : NetworkMessage() {
	var quantity: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		quantity = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 8121
}
