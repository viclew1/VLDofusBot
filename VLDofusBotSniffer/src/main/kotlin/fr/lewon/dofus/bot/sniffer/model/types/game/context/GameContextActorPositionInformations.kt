package fr.lewon.dofus.bot.sniffer.model.types.game.context

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameContextActorPositionInformations : NetworkType() {
	var contextualId: Double = 0.0
	lateinit var disposition: EntityDispositionInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		contextualId = stream.readDouble().toDouble()
		disposition = ProtocolTypeManager.getInstance<EntityDispositionInformations>(stream.readUnsignedShort())
		disposition.deserialize(stream)
	}
}
