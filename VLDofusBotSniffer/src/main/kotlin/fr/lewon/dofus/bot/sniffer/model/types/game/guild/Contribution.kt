package fr.lewon.dofus.bot.sniffer.model.types.game.guild

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class Contribution : NetworkType() {
	var contributorId: Double = 0.0
	var contributorName: String = ""
	var amount: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		contributorId = stream.readVarLong().toDouble()
		contributorName = stream.readUTF()
		amount = stream.readVarLong().toDouble()
	}
}
