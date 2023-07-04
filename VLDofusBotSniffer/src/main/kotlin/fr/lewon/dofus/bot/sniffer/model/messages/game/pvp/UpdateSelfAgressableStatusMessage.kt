package fr.lewon.dofus.bot.sniffer.model.messages.game.pvp

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class UpdateSelfAgressableStatusMessage : NetworkMessage() {
	var status: Int = 0
	var probationTime: Double = 0.0
	var roleAvAId: Int = 0
	var pictoScore: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		status = stream.readUnsignedByte().toInt()
		probationTime = stream.readDouble().toDouble()
		roleAvAId = stream.readInt().toInt()
		pictoScore = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 8145
}
