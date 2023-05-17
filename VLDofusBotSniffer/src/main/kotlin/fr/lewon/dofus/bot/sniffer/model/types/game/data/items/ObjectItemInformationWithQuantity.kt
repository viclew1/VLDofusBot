package fr.lewon.dofus.bot.sniffer.model.types.game.data.items

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects.ObjectEffect
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ObjectItemInformationWithQuantity : ObjectItemMinimalInformation() {
	var quantity: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		quantity = stream.readVarInt().toInt()
	}
}
