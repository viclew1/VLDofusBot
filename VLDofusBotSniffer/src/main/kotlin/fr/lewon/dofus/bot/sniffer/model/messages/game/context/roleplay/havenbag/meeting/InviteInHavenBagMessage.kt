package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.havenbag.meeting

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterMinimalInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class InviteInHavenBagMessage : NetworkMessage() {
	lateinit var guestInformations: CharacterMinimalInformations
	var accept: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guestInformations = CharacterMinimalInformations()
		guestInformations.deserialize(stream)
		accept = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 5865
}
