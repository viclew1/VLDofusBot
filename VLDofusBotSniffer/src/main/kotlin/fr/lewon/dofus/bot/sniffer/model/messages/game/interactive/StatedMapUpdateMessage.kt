package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive

import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.StatedElement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class StatedMapUpdateMessage : NetworkMessage() {
	var statedElements: ArrayList<StatedElement> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		statedElements = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = StatedElement()
			item.deserialize(stream)
			statedElements.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 8533
}
