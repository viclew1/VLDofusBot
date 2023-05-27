package fr.lewon.dofus.bot.sniffer.model.messages.game.character.deletion

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterDeletionPrepareRequestMessage : NetworkMessage() {
	var characterId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		characterId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 5134
}
