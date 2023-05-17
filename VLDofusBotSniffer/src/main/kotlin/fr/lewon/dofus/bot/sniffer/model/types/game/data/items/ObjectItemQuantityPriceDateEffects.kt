package fr.lewon.dofus.bot.sniffer.model.types.game.data.items

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ObjectItemQuantityPriceDateEffects : ObjectItemGenericQuantity() {
	var price: Double = 0.0
	lateinit var effects: ObjectEffects
	var date: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		price = stream.readVarLong().toDouble()
		effects = ObjectEffects()
		effects.deserialize(stream)
		date = stream.readInt().toInt()
	}
}
