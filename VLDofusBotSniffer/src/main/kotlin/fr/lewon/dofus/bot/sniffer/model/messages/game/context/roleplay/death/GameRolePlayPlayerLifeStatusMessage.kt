package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.death

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayPlayerLifeStatusMessage : NetworkMessage() {
	var state: Int = 0
	var phenixMapId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		state = stream.readUnsignedByte().toInt()
		phenixMapId = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 8105
}
