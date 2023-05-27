package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.havenbag.meeting

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterMinimalInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class InviteInHavenBagClosedMessage : NetworkMessage() {
	lateinit var hostInformations: CharacterMinimalInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		hostInformations = CharacterMinimalInformations()
		hostInformations.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 62
}
