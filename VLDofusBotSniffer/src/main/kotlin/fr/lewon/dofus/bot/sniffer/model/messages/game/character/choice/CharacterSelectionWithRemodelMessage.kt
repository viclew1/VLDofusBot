package fr.lewon.dofus.bot.sniffer.model.messages.game.character.choice

import fr.lewon.dofus.bot.sniffer.model.types.game.character.choice.RemodelingInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterSelectionWithRemodelMessage : CharacterSelectionMessage() {
	lateinit var remodel: RemodelingInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		remodel = RemodelingInformation()
		remodel.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 4181
}
