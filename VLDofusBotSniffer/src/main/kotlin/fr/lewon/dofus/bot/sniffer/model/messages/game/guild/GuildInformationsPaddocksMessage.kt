package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.paddock.PaddockContentInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildInformationsPaddocksMessage : NetworkMessage() {
	var nbPaddockMax: Int = 0
	var paddocksInformations: ArrayList<PaddockContentInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		nbPaddockMax = stream.readUnsignedByte().toInt()
		paddocksInformations = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = PaddockContentInformations()
			item.deserialize(stream)
			paddocksInformations.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 7863
}
