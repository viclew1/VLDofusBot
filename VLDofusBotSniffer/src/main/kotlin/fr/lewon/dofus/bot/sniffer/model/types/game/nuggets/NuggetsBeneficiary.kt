package fr.lewon.dofus.bot.sniffer.model.types.game.nuggets

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class NuggetsBeneficiary : NetworkType() {
	var beneficiaryPlayerId: Double = 0.0
	var nuggetsQuantity: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		beneficiaryPlayerId = stream.readVarLong().toDouble()
		nuggetsQuantity = stream.readInt().toInt()
	}
}
