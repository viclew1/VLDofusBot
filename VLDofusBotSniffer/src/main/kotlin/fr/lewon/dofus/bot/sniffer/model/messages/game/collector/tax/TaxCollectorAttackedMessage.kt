package fr.lewon.dofus.bot.sniffer.model.messages.game.collector.tax

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.BasicAllianceInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorAttackedMessage : NetworkMessage() {
	var firstNameId: Int = 0
	var lastNameId: Int = 0
	var worldX: Int = 0
	var worldY: Int = 0
	var mapId: Double = 0.0
	var subAreaId: Int = 0
	lateinit var alliance: BasicAllianceInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		firstNameId = stream.readVarShort().toInt()
		lastNameId = stream.readVarShort().toInt()
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		mapId = stream.readDouble().toDouble()
		subAreaId = stream.readVarShort().toInt()
		alliance = BasicAllianceInformations()
		alliance.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 6371
}
