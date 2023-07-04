package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive.meeting

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TeleportBuddiesRequestedMessage : NetworkMessage() {
	var dungeonId: Int = 0
	var inviterId: Double = 0.0
	var invalidBuddiesIds: ArrayList<Double> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		dungeonId = stream.readVarShort().toInt()
		inviterId = stream.readVarLong().toDouble()
		invalidBuddiesIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarLong().toDouble()
			invalidBuddiesIds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9327
}
