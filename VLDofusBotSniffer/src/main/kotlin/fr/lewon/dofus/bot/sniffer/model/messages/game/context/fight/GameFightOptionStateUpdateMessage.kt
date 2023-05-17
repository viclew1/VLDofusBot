package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightOptionStateUpdateMessage : NetworkMessage() {
	var fightId: Int = 0
	var teamId: Int = 0
	var option: Int = 0
	var state: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fightId = stream.readVarShort().toInt()
		teamId = stream.readUnsignedByte().toInt()
		option = stream.readUnsignedByte().toInt()
		state = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 4152
}
