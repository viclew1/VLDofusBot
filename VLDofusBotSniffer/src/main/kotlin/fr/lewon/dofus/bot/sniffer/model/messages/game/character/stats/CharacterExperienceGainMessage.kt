package fr.lewon.dofus.bot.sniffer.model.messages.game.character.stats

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterExperienceGainMessage : NetworkMessage() {
	var experienceCharacter: Double = 0.0
	var experienceMount: Double = 0.0
	var experienceGuild: Double = 0.0
	var experienceIncarnation: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		experienceCharacter = stream.readVarLong().toDouble()
		experienceMount = stream.readVarLong().toDouble()
		experienceGuild = stream.readVarLong().toDouble()
		experienceIncarnation = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 3140
}
