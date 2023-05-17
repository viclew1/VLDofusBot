package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightEffectTriggerCount : NetworkType() {
	var effectId: Int = 0
	var targetId: Double = 0.0
	var count: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		effectId = stream.readVarInt().toInt()
		targetId = stream.readDouble().toDouble()
		count = stream.readUnsignedShort().toInt()
	}
}
