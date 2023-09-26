package fr.lewon.dofus.bot.sniffer.model.messages.game.prism

import fr.lewon.dofus.bot.sniffer.model.types.game.prism.PrismGeolocalizedInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PrismsListMessage : NetworkMessage() {
	var prisms: ArrayList<PrismGeolocalizedInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		prisms = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<PrismGeolocalizedInformation>(stream.readUnsignedShort())
			item.deserialize(stream)
			prisms.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1417
}
