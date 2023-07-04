package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightOptionToggleMessage : NetworkMessage() {
	var option: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		option = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 4020
}
