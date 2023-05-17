package fr.lewon.dofus.bot.sniffer.model.types.game.house

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HouseInstanceInformations : NetworkType() {
	var secondHand: Boolean = false
	var isLocked: Boolean = false
	var hasOwner: Boolean = false
	var isSaleLocked: Boolean = false
	var isAdminLocked: Boolean = false
	var instanceId: Int = 0
	lateinit var ownerTag: AccountTagInformation
	var price: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		secondHand = BooleanByteWrapper.getFlag(_box0, 0)
		isLocked = BooleanByteWrapper.getFlag(_box0, 1)
		hasOwner = BooleanByteWrapper.getFlag(_box0, 2)
		isSaleLocked = BooleanByteWrapper.getFlag(_box0, 3)
		isAdminLocked = BooleanByteWrapper.getFlag(_box0, 4)
		instanceId = stream.readInt().toInt()
		ownerTag = AccountTagInformation()
		ownerTag.deserialize(stream)
		price = stream.readVarLong().toDouble()
	}
}
