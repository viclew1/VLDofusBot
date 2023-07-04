package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.stats

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class StatsUpgradeResultMessage : NetworkMessage() {
	var result: Int = 0
	var nbCharacBoost: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		result = stream.readUnsignedByte().toInt()
		nbCharacBoost = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 7979
}
