package fr.lewon.dofus.bot.sniffer.model.types.game.context

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MapCoordinates : NetworkType() {
	var worldX: Int = 0
	var worldY: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
	}
}
