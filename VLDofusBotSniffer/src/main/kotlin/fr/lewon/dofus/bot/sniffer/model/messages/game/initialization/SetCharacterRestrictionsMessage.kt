package fr.lewon.dofus.bot.sniffer.model.messages.game.initialization

import fr.lewon.dofus.bot.sniffer.model.types.game.character.restriction.ActorRestrictionsInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SetCharacterRestrictionsMessage : NetworkMessage() {
	var actorId: Double = 0.0
	lateinit var restrictions: ActorRestrictionsInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		actorId = stream.readDouble().toDouble()
		restrictions = ActorRestrictionsInformations()
		restrictions.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 2350
}
