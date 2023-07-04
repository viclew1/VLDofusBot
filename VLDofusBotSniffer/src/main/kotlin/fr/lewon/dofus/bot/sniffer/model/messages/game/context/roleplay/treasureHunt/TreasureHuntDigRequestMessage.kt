package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.treasureHunt

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TreasureHuntDigRequestMessage : NetworkMessage() {
	var questType: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		questType = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 1139
}
