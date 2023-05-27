package fr.lewon.dofus.bot.sniffer.model.messages.game.atlas.compass

import fr.lewon.dofus.bot.sniffer.model.types.game.context.MapCoordinates
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CompassUpdateMessage : NetworkMessage() {
	var type: Int = 0
	lateinit var coords: MapCoordinates
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		type = stream.readUnsignedByte().toInt()
		coords = ProtocolTypeManager.getInstance<MapCoordinates>(stream.readUnsignedShort())
		coords.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 6807
}
