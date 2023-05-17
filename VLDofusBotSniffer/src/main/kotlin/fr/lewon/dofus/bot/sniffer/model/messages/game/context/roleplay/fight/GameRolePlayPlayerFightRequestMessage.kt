package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayPlayerFightRequestMessage : NetworkMessage() {
	var targetId: Double = 0.0
	var targetCellId: Int = 0
	var friendly: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		targetId = stream.readVarLong().toDouble()
		targetCellId = stream.readUnsignedShort().toInt()
		friendly = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 6454
}
