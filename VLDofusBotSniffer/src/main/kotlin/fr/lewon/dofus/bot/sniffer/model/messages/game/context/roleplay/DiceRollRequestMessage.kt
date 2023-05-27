package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DiceRollRequestMessage : NetworkMessage() {
	var dice: Int = 0
	var faces: Int = 0
	var channel: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		dice = stream.readVarInt().toInt()
		faces = stream.readVarInt().toInt()
		channel = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 7241
}
