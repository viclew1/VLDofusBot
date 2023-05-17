package fr.lewon.dofus.bot.sniffer.model.types.game.paddock

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockBuyableInformations : NetworkType() {
	var price: Double = 0.0
	var locked: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		price = stream.readVarLong().toDouble()
		locked = stream.readBoolean()
	}
}
