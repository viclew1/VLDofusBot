package fr.lewon.dofus.bot.sniffer.model.types.game.house

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HouseInformationsInside : HouseInformations() {
	lateinit var houseInfos: HouseInstanceInformations
	var worldX: Int = 0
	var worldY: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		houseInfos = ProtocolTypeManager.getInstance<HouseInstanceInformations>(stream.readUnsignedShort())
		houseInfos.deserialize(stream)
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
	}
}
