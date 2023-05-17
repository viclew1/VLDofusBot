package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.purchasable

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PurchasableDialogMessage : NetworkMessage() {
	var buyOrSell: Boolean = false
	var secondHand: Boolean = false
	var purchasableId: Double = 0.0
	var purchasableInstanceId: Int = 0
	var price: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		buyOrSell = BooleanByteWrapper.getFlag(_box0, 0)
		secondHand = BooleanByteWrapper.getFlag(_box0, 1)
		purchasableId = stream.readDouble().toDouble()
		purchasableInstanceId = stream.readInt().toInt()
		price = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 8325
}
