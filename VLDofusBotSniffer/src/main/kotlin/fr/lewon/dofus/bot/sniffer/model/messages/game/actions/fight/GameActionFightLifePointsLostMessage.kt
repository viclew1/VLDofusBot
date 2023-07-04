package fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight

import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.AbstractGameActionMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionFightLifePointsLostMessage : AbstractGameActionMessage() {
	var targetId: Double = 0.0
	var loss: Int = 0
	var permanentDamages: Int = 0
	var elementId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		targetId = stream.readDouble().toDouble()
		loss = stream.readVarInt().toInt()
		permanentDamages = stream.readVarInt().toInt()
		elementId = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 8706
}
