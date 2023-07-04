package fr.lewon.dofus.bot.sniffer.model.messages.game.tinsel

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class OrnamentSelectRequestMessage : NetworkMessage() {
	var ornamentId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		ornamentId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 3742
}
