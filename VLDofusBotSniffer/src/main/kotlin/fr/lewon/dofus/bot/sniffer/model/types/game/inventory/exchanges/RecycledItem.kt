package fr.lewon.dofus.bot.sniffer.model.types.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class RecycledItem : NetworkType() {
	var id: Int = 0
	var qty: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readVarInt().toInt()
		qty = stream.readInt().toInt()
	}
}
