package fr.lewon.dofus.bot.sniffer.model.messages.game.character.stats

import fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic.CharacterCharacteristicsInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterStatsListMessage : NetworkMessage() {
	lateinit var stats: CharacterCharacteristicsInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		stats = CharacterCharacteristicsInformations()
		stats.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 1536
}
