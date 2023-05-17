package fr.lewon.dofus.bot.sniffer.model.types.game.actions.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionMarkedCell : NetworkType() {
	var cellId: Int = 0
	var zoneSize: Int = 0
	var cellColor: Int = 0
	var cellsType: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		cellId = stream.readVarShort().toInt()
		zoneSize = stream.readUnsignedByte().toInt()
		cellColor = stream.readInt().toInt()
		cellsType = stream.readUnsignedByte().toInt()
	}
}
