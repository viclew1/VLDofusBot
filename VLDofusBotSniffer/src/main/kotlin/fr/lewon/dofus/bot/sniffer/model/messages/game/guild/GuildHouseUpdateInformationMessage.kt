package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.house.HouseInformationsForGuild
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildHouseUpdateInformationMessage : NetworkMessage() {
	lateinit var housesInformations: HouseInformationsForGuild
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		housesInformations = HouseInformationsForGuild()
		housesInformations.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 5756
}
