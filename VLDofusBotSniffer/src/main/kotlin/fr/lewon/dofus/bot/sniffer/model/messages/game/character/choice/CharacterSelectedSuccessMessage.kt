package fr.lewon.dofus.bot.sniffer.model.messages.game.character.choice

import fr.lewon.dofus.bot.sniffer.model.types.game.character.choice.CharacterBaseInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterSelectedSuccessMessage : NetworkMessage() {
	lateinit var infos: CharacterBaseInformations
	var isCollectingStats: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		infos = CharacterBaseInformations()
		infos.deserialize(stream)
		isCollectingStats = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 2543
}
