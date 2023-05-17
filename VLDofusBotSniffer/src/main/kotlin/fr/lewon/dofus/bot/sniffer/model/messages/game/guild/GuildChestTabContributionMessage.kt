package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildChestTabContributionMessage : NetworkMessage() {
	var tabNumber: Int = 0
	var requiredAmount: Double = 0.0
	var currentAmount: Double = 0.0
	var chestContributionEnrollmentDelay: Double = 0.0
	var chestContributionDelay: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		tabNumber = stream.readVarInt().toInt()
		requiredAmount = stream.readVarLong().toDouble()
		currentAmount = stream.readVarLong().toDouble()
		chestContributionEnrollmentDelay = stream.readDouble().toDouble()
		chestContributionDelay = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 6766
}
