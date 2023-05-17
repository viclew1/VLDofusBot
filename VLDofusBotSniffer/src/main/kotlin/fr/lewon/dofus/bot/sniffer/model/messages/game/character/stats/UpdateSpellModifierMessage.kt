package fr.lewon.dofus.bot.sniffer.model.messages.game.character.stats

import fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic.CharacterSpellModification
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class UpdateSpellModifierMessage : NetworkMessage() {
	var actorId: Double = 0.0
	lateinit var spellModifier: CharacterSpellModification
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		actorId = stream.readDouble().toDouble()
		spellModifier = CharacterSpellModification()
		spellModifier.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 8033
}
