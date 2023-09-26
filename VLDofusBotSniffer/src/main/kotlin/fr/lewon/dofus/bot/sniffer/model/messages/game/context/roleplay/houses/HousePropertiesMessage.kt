package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.houses

import fr.lewon.dofus.bot.sniffer.model.types.game.house.HouseInstanceInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HousePropertiesMessage : NetworkMessage() {
	var houseId: Int = 0
	var doorsOnMap: ArrayList<Int> = ArrayList()
	lateinit var properties: HouseInstanceInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		houseId = stream.readVarInt().toInt()
		doorsOnMap = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			doorsOnMap.add(item)
		}
		properties = ProtocolTypeManager.getInstance<HouseInstanceInformations>(stream.readUnsignedShort())
		properties.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 9986
}
