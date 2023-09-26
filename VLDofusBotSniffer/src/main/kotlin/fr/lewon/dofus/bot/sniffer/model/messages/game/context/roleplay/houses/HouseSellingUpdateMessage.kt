package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.houses

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HouseSellingUpdateMessage : NetworkMessage() {
	var houseId: Int = 0
	var instanceId: Int = 0
	var secondHand: Boolean = false
	var realPrice: Double = 0.0
	lateinit var buyerTag: AccountTagInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		houseId = stream.readVarInt().toInt()
		instanceId = stream.readInt().toInt()
		secondHand = stream.readBoolean()
		realPrice = stream.readVarLong().toDouble()
		buyerTag = AccountTagInformation()
		buyerTag.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 7266
}
