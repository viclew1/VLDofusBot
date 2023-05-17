package fr.lewon.dofus.bot.sniffer.model.types.game.house

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HouseInformationsForSell : NetworkType() {
	var instanceId: Int = 0
	var secondHand: Boolean = false
	var modelId: Int = 0
	lateinit var ownerTag: AccountTagInformation
	var hasOwner: Boolean = false
	var ownerCharacterName: String = ""
	var worldX: Int = 0
	var worldY: Int = 0
	var subAreaId: Int = 0
	var nbRoom: Int = 0
	var nbChest: Int = 0
	var skillListIds: ArrayList<Int> = ArrayList()
	var isLocked: Boolean = false
	var price: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		instanceId = stream.readInt().toInt()
		secondHand = stream.readBoolean()
		modelId = stream.readVarInt().toInt()
		ownerTag = AccountTagInformation()
		ownerTag.deserialize(stream)
		hasOwner = stream.readBoolean()
		ownerCharacterName = stream.readUTF()
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		subAreaId = stream.readVarShort().toInt()
		nbRoom = stream.readUnsignedByte().toInt()
		nbChest = stream.readUnsignedByte().toInt()
		skillListIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			skillListIds.add(item)
		}
		isLocked = stream.readBoolean()
		price = stream.readVarLong().toDouble()
	}
}
