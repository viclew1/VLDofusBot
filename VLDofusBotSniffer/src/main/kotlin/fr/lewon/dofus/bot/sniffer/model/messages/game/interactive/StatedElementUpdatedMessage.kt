package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive

import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.StatedElement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class StatedElementUpdatedMessage : NetworkMessage() {
	lateinit var statedElement: StatedElement
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		statedElement = StatedElement()
		statedElement.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 5876
}
