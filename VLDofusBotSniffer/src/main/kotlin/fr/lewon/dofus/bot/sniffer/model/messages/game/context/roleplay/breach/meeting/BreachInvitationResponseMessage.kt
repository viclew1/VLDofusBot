package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.breach.meeting

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterMinimalInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BreachInvitationResponseMessage : NetworkMessage() {
	lateinit var guest: CharacterMinimalInformations
	var accept: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guest = CharacterMinimalInformations()
		guest.deserialize(stream)
		accept = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 9038
}
