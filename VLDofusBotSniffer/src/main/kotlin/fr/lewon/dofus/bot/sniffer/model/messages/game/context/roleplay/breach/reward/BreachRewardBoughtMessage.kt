package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.breach.reward

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BreachRewardBoughtMessage : NetworkMessage() {
	var id: Int = 0
	var bought: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readVarInt().toInt()
		bought = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 9202
}
