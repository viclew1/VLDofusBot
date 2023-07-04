package fr.lewon.dofus.bot.sniffer.model.messages.game.character.spellmodifier

import fr.lewon.dofus.bot.sniffer.model.types.game.character.spellmodifier.SpellModifierMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ApplySpellModifierMessage : NetworkMessage() {
	var actorId: Double = 0.0
	lateinit var modifier: SpellModifierMessage
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		actorId = stream.readDouble().toDouble()
		modifier = SpellModifierMessage()
		modifier.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 9665
}
