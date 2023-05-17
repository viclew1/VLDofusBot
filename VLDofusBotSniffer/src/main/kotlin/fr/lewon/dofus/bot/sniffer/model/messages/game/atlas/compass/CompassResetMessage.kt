package fr.lewon.dofus.bot.sniffer.model.messages.game.atlas.compass

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CompassResetMessage : NetworkMessage() {
	var type: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		type = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 1538
}
