package fr.lewon.dofus.bot.sniffer.model.types.game.interactive

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class StatedElement : NetworkType() {
	var elementId: Int = 0
	var elementCellId: Int = 0
	var elementState: Int = 0
	var onCurrentMap: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		elementId = stream.readInt().toInt()
		elementCellId = stream.readVarShort().toInt()
		elementState = stream.readVarInt().toInt()
		onCurrentMap = stream.readBoolean()
	}
}
