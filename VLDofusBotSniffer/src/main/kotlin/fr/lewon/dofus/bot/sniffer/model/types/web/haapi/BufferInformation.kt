package fr.lewon.dofus.bot.sniffer.model.types.web.haapi

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BufferInformation : NetworkType() {
	var id: Double = 0.0
	var amount: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readVarLong().toDouble()
		amount = stream.readVarLong().toDouble()
	}
}
