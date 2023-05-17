package fr.lewon.dofus.bot.sniffer.model.messages.debug

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DebugHighlightCellsMessage : NetworkMessage() {
	var color: Double = 0.0
	var cells: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		color = stream.readDouble().toDouble()
		cells = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			cells.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 5323
}
