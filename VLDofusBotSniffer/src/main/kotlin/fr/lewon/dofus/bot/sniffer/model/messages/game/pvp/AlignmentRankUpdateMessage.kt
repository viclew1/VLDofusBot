package fr.lewon.dofus.bot.sniffer.model.messages.game.pvp

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AlignmentRankUpdateMessage : NetworkMessage() {
	var alignmentRank: Int = 0
	var verbose: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		alignmentRank = stream.readUnsignedByte().toInt()
		verbose = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 3325
}
