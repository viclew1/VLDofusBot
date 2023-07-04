package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.delay

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayDelayedActionFinishedMessage : NetworkMessage() {
	var delayedCharacterId: Double = 0.0
	var delayTypeId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		delayedCharacterId = stream.readDouble().toDouble()
		delayTypeId = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 4342
}
