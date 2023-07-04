package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class InteractiveUsedMessage : NetworkMessage() {
	var entityId: Double = 0.0
	var elemId: Int = 0
	var skillId: Int = 0
	var duration: Int = 0
	var canMove: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		entityId = stream.readVarLong().toDouble()
		elemId = stream.readVarInt().toInt()
		skillId = stream.readVarShort().toInt()
		duration = stream.readVarShort().toInt()
		canMove = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 8940
}
