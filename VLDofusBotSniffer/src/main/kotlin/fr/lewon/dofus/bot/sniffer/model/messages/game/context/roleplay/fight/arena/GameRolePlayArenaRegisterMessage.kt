package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayArenaRegisterMessage : NetworkMessage() {
	var battleMode: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		battleMode = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 888
}
