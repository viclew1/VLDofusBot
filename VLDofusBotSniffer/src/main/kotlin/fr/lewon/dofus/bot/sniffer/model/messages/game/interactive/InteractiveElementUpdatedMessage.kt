package fr.lewon.dofus.bot.sniffer.model.messages.game.interactive

import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class InteractiveElementUpdatedMessage : NetworkMessage() {
	lateinit var interactiveElement: InteractiveElement
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		interactiveElement = InteractiveElement()
		interactiveElement.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 9569
}
