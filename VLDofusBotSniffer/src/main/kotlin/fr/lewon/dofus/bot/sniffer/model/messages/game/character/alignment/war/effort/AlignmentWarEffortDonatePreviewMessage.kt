package fr.lewon.dofus.bot.sniffer.model.messages.game.character.alignment.war.effort

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AlignmentWarEffortDonatePreviewMessage : NetworkMessage() {
	var xp: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		xp = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 9403
}
