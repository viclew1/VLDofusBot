package fr.lewon.dofus.bot.sniffer.model.messages.debug

import fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic.CharacterCharacteristics
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DumpedEntityStatsMessage : NetworkMessage() {
	var actorId: Double = 0.0
	lateinit var stats: CharacterCharacteristics
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		actorId = stream.readDouble().toDouble()
		stats = CharacterCharacteristics()
		stats.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 9489
}
