package fr.lewon.dofus.bot.sniffer.model.types.game.prism

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PrismInformation : NetworkType() {
	var state: Int = 0
	var placementDate: Int = 0
	var nuggetsCount: Int = 0
	var durability: Int = 0
	var nextEvolutionDate: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		state = stream.readUnsignedByte().toInt()
		placementDate = stream.readInt().toInt()
		nuggetsCount = stream.readVarInt().toInt()
		durability = stream.readInt().toInt()
		nextEvolutionDate = stream.readDouble().toDouble()
	}
}
