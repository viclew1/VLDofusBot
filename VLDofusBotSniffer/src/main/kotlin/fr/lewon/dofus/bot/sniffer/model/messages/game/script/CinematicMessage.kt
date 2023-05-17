package fr.lewon.dofus.bot.sniffer.model.messages.game.script

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CinematicMessage : NetworkMessage() {
	var cinematicId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		cinematicId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 965
}
