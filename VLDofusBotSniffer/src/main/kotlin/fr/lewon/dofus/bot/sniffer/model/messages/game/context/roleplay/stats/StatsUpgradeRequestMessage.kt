package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.stats

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class StatsUpgradeRequestMessage : NetworkMessage() {
	var useAdditionnal: Boolean = false
	var statId: Int = 0
	var boostPoint: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		useAdditionnal = stream.readBoolean()
		statId = stream.readUnsignedByte().toInt()
		boostPoint = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 1404
}
