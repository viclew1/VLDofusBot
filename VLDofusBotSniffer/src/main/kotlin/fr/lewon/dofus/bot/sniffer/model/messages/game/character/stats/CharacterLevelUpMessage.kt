package fr.lewon.dofus.bot.sniffer.model.messages.game.character.stats

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterLevelUpMessage : NetworkMessage() {
	var newLevel: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		newLevel = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 246
}
