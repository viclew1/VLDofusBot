package fr.lewon.dofus.bot.sniffer.model.messages.game.context.display

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DisplayNumericalValuePaddockMessage : NetworkMessage() {
	var rideId: Int = 0
	var value: Int = 0
	var type: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		rideId = stream.readInt().toInt()
		value = stream.readInt().toInt()
		type = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 9965
}
