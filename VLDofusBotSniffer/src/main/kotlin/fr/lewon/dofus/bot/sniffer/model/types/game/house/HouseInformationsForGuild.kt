package fr.lewon.dofus.bot.sniffer.model.types.game.house

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HouseInformationsForGuild : HouseInformations() {
	var instanceId: Int = 0
	var secondHand: Boolean = false
	lateinit var ownerTag: AccountTagInformation
	var worldX: Int = 0
	var worldY: Int = 0
	var mapId: Double = 0.0
	var subAreaId: Int = 0
	var skillListIds: ArrayList<Int> = ArrayList()
	var guildshareParams: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		instanceId = stream.readInt().toInt()
		secondHand = stream.readBoolean()
		ownerTag = AccountTagInformation()
		ownerTag.deserialize(stream)
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		mapId = stream.readDouble().toDouble()
		subAreaId = stream.readVarShort().toInt()
		skillListIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			skillListIds.add(item)
		}
		guildshareParams = stream.readVarInt().toInt()
	}
}
