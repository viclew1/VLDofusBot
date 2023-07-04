package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive

import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class InteractiveMapUpdateMessage : NetworkMessage() {
	var interactiveElements: ArrayList<InteractiveElement> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		interactiveElements = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<InteractiveElement>(stream.readUnsignedShort())
			item.deserialize(stream)
			interactiveElements.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 2157
}
