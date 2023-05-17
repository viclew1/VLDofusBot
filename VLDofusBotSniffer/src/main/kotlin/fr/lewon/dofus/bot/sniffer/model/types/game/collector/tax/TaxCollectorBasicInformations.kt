package fr.lewon.dofus.bot.sniffer.model.types.game.collector.tax

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorBasicInformations : NetworkType() {
	var firstNameId: Int = 0
	var lastNameId: Int = 0
	var worldX: Int = 0
	var worldY: Int = 0
	var mapId: Double = 0.0
	var subAreaId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		firstNameId = stream.readVarShort().toInt()
		lastNameId = stream.readVarShort().toInt()
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		mapId = stream.readDouble().toDouble()
		subAreaId = stream.readVarShort().toInt()
	}
}
