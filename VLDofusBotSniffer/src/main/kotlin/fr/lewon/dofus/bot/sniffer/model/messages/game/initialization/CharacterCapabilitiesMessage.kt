package fr.lewon.dofus.bot.sniffer.model.messages.game.initialization

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterCapabilitiesMessage : NetworkMessage() {
	var guildEmblemSymbolCategories: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guildEmblemSymbolCategories = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 8946
}
