package fr.lewon.dofus.bot.sniffer.model.messages.game.context

import fr.lewon.dofus.bot.sniffer.model.types.game.context.IdentifiedEntityDispositionInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameEntityDispositionMessage : NetworkMessage() {
	lateinit var disposition: IdentifiedEntityDispositionInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		disposition = IdentifiedEntityDispositionInformations()
		disposition.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 7816
}
