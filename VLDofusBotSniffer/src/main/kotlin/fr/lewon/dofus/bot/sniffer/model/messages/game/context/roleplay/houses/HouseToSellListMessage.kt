package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.houses

import fr.lewon.dofus.bot.sniffer.model.types.game.house.HouseInformationsForSell
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HouseToSellListMessage : NetworkMessage() {
	var pageIndex: Int = 0
	var totalPage: Int = 0
	var houseList: ArrayList<HouseInformationsForSell> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		pageIndex = stream.readVarShort().toInt()
		totalPage = stream.readVarShort().toInt()
		houseList = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = HouseInformationsForSell()
			item.deserialize(stream)
			houseList.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1247
}
