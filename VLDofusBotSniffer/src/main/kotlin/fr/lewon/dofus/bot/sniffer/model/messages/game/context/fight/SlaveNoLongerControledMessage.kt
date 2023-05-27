package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SlaveNoLongerControledMessage : NetworkMessage() {
	var masterId: Double = 0.0
	var slaveId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		masterId = stream.readDouble().toDouble()
		slaveId = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 6277
}
