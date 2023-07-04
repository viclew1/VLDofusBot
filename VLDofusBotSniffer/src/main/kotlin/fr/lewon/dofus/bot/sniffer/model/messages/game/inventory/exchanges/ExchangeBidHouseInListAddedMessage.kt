package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects.ObjectEffect
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeBidHouseInListAddedMessage : NetworkMessage() {
	var itemUID: Int = 0
	var objectGID: Int = 0
	var objectType: Int = 0
	var effects: ArrayList<ObjectEffect> = ArrayList()
	var prices: ArrayList<Double> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		itemUID = stream.readInt().toInt()
		objectGID = stream.readVarInt().toInt()
		objectType = stream.readInt().toInt()
		effects = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<ObjectEffect>(stream.readUnsignedShort())
			item.deserialize(stream)
			effects.add(item)
		}
		prices = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarLong().toDouble()
			prices.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1990
}
