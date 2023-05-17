package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.houses

import fr.lewon.dofus.bot.sniffer.model.types.game.house.AccountHouseInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AccountHouseMessage : NetworkMessage() {
	var houses: ArrayList<AccountHouseInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		houses = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = AccountHouseInformations()
			item.deserialize(stream)
			houses.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1572
}
