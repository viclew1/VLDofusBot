package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.treasureHunt

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TreasureHuntLegendaryRequestMessage : NetworkMessage() {
	var legendaryId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		legendaryId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 9671
}
