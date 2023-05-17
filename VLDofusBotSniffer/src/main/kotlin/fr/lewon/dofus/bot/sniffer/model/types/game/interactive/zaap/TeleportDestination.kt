package fr.lewon.dofus.bot.sniffer.model.types.game.interactive.zaap

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TeleportDestination : NetworkType() {
	var type: Int = 0
	var mapId: Double = 0.0
	var subAreaId: Int = 0
	var level: Int = 0
	var cost: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		type = stream.readUnsignedByte().toInt()
		mapId = stream.readDouble().toDouble()
		subAreaId = stream.readVarShort().toInt()
		level = stream.readVarShort().toInt()
		cost = stream.readVarShort().toInt()
	}
}
