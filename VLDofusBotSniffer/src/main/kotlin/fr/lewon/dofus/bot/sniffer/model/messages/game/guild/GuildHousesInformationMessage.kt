package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.house.HouseInformationsForGuild
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildHousesInformationMessage : NetworkMessage() {
	var housesInformations: ArrayList<HouseInformationsForGuild> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		housesInformations = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = HouseInformationsForGuild()
			item.deserialize(stream)
			housesInformations.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1134
}
