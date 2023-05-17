package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayPlayerFightFriendlyAnsweredMessage : NetworkMessage() {
	var fightId: Int = 0
	var sourceId: Double = 0.0
	var targetId: Double = 0.0
	var accept: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fightId = stream.readVarShort().toInt()
		sourceId = stream.readVarLong().toDouble()
		targetId = stream.readVarLong().toDouble()
		accept = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 4070
}
