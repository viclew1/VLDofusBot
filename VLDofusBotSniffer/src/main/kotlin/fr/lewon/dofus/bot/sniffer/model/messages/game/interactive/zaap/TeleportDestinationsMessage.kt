package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive.zaap

import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.zaap.TeleportDestination
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TeleportDestinationsMessage : NetworkMessage() {
	var type: Int = 0
	var destinations: ArrayList<TeleportDestination> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		type = stream.readUnsignedByte().toInt()
		destinations = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = TeleportDestination()
			item.deserialize(stream)
			destinations.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 2118
}
