package fr.lewon.dofus.bot.sniffer.model.types.game.pvp

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AgressableStatusMessage : NetworkType() {
	var playerId: Double = 0.0
	var enable: Int = 0
	var roleAvAId: Int = 0
	var pictoScore: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerId = stream.readVarLong().toDouble()
		enable = stream.readUnsignedByte().toInt()
		roleAvAId = stream.readInt().toInt()
		pictoScore = stream.readInt().toInt()
	}
}
