package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.MapCoordinatesExtended
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AtlasPointsInformations : NetworkType() {
	var type: Int = 0
	var coords: ArrayList<MapCoordinatesExtended> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		type = stream.readUnsignedByte().toInt()
		coords = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = MapCoordinatesExtended()
			item.deserialize(stream)
			coords.add(item)
		}
	}
}
