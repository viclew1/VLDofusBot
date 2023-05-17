package fr.lewon.dofus.bot.sniffer.model.types.game.interactive

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MapObstacle : NetworkType() {
	var obstacleCellId: Int = 0
	var state: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		obstacleCellId = stream.readVarShort().toInt()
		state = stream.readUnsignedByte().toInt()
	}
}
