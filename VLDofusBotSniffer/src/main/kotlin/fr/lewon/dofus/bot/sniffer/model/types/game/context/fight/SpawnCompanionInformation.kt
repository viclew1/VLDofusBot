package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SpawnCompanionInformation : SpawnInformation() {
	var modelId: Int = 0
	var level: Int = 0
	var summonerId: Double = 0.0
	var ownerId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		modelId = stream.readUnsignedByte().toInt()
		level = stream.readVarShort().toInt()
		summonerId = stream.readDouble().toDouble()
		ownerId = stream.readDouble().toDouble()
	}
}
