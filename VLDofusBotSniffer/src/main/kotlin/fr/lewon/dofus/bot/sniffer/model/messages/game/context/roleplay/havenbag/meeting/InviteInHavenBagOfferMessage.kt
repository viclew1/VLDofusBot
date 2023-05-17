package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.havenbag.meeting

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterMinimalInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class InviteInHavenBagOfferMessage : NetworkMessage() {
	lateinit var hostInformations: CharacterMinimalInformations
	var timeLeftBeforeCancel: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		hostInformations = CharacterMinimalInformations()
		hostInformations.deserialize(stream)
		timeLeftBeforeCancel = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 2399
}
