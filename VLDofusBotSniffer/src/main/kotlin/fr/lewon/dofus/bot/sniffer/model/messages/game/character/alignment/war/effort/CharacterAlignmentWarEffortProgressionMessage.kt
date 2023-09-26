package fr.lewon.dofus.bot.sniffer.model.messages.game.character.alignment.war.effort

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterAlignmentWarEffortProgressionMessage : NetworkMessage() {
	var alignmentWarEffortDailyLimit: Double = 0.0
	var alignmentWarEffortDailyDonation: Double = 0.0
	var alignmentWarEffortPersonalDonation: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		alignmentWarEffortDailyLimit = stream.readVarLong().toDouble()
		alignmentWarEffortDailyDonation = stream.readVarLong().toDouble()
		alignmentWarEffortPersonalDonation = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 7746
}
