package fr.lewon.dofus.bot.sniffer.model.messages.game.idol

import fr.lewon.dofus.bot.sniffer.model.types.game.idol.Idol
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IdolFightPreparationUpdateMessage : NetworkMessage() {
	var idolSource: Int = 0
	var idols: ArrayList<Idol> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		idolSource = stream.readUnsignedByte().toInt()
		idols = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<Idol>(stream.readUnsignedShort())
			item.deserialize(stream)
			idols.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 8314
}
