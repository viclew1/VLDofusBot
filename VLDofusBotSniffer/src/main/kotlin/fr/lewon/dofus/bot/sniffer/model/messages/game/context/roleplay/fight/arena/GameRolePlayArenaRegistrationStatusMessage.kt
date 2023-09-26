package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayArenaRegistrationStatusMessage : NetworkMessage() {
	var registered: Boolean = false
	var step: Int = 0
	var battleMode: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		registered = stream.readBoolean()
		step = stream.readUnsignedByte().toInt()
		battleMode = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 2179
}
