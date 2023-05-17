package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.MapObstacle
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MapObstacleUpdateMessage : NetworkMessage() {
	var obstacles: ArrayList<MapObstacle> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		obstacles = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = MapObstacle()
			item.deserialize(stream)
			obstacles.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 6038
}
