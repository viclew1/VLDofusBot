package fr.lewon.dofus.bot.sniffer.model.messages.game.basic

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CurrentServerStatusUpdateMessage : NetworkMessage() {
	var status: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		status = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 4697
}
