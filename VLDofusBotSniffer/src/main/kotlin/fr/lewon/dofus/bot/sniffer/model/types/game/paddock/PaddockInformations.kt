package fr.lewon.dofus.bot.sniffer.model.types.game.paddock

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockInformations : NetworkType() {
	var maxOutdoorMount: Int = 0
	var maxItems: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		maxOutdoorMount = stream.readVarShort().toInt()
		maxItems = stream.readVarShort().toInt()
	}
}
