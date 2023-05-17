package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.idol.Idol
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightStartMessage : NetworkMessage() {
	var idols: ArrayList<Idol> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		idols = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = Idol()
			item.deserialize(stream)
			idols.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4014
}
