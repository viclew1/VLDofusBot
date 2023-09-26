package fr.lewon.dofus.bot.sniffer.model.messages.game.character.alteration

import fr.lewon.dofus.bot.sniffer.model.types.game.character.alteration.AlterationInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AlterationAddedMessage : NetworkMessage() {
	lateinit var alteration: AlterationInfo
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		alteration = AlterationInfo()
		alteration.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 2682
}
