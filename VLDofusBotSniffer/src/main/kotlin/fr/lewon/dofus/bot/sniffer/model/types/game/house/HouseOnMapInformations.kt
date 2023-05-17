package fr.lewon.dofus.bot.sniffer.model.types.game.house

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HouseOnMapInformations : HouseInformations() {
	var doorsOnMap: ArrayList<Int> = ArrayList()
	var houseInstances: ArrayList<HouseInstanceInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		doorsOnMap = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			doorsOnMap.add(item)
		}
		houseInstances = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = HouseInstanceInformations()
			item.deserialize(stream)
			houseInstances.add(item)
		}
	}
}
