package fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MountInformationRequestMessage : NetworkMessage() {
	var id: Double = 0.0
	var time: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readDouble().toDouble()
		time = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 4256
}
