package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job

import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobCrafterDirectoryEntryPlayerInfo : NetworkType() {
	var playerId: Double = 0.0
	var playerName: String = ""
	var alignmentSide: Int = 0
	var breed: Int = 0
	var sex: Boolean = false
	var isInWorkshop: Boolean = false
	var worldX: Int = 0
	var worldY: Int = 0
	var mapId: Double = 0.0
	var subAreaId: Int = 0
	var canCraftLegendary: Boolean = false
	lateinit var status: PlayerStatus
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerId = stream.readVarLong().toDouble()
		playerName = stream.readUTF()
		alignmentSide = stream.readUnsignedByte().toInt()
		breed = stream.readUnsignedByte().toInt()
		sex = stream.readBoolean()
		isInWorkshop = stream.readBoolean()
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		mapId = stream.readDouble().toDouble()
		subAreaId = stream.readVarShort().toInt()
		canCraftLegendary = stream.readBoolean()
		status = ProtocolTypeManager.getInstance<PlayerStatus>(stream.readUnsignedShort())
		status.deserialize(stream)
	}
}
