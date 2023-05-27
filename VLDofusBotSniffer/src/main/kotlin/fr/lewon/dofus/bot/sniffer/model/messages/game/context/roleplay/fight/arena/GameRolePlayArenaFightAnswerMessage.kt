package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayArenaFightAnswerMessage : NetworkMessage() {
	var fightId: Int = 0
	var accept: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fightId = stream.readVarShort().toInt()
		accept = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 1730
}
