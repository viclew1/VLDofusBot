package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.breach.meeting

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterMinimalInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BreachKickResponseMessage : NetworkMessage() {
	lateinit var target: CharacterMinimalInformations
	var kicked: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		target = CharacterMinimalInformations()
		target.deserialize(stream)
		kicked = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 6416
}
