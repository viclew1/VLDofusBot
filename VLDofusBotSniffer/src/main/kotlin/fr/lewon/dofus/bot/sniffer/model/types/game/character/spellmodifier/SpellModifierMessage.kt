package fr.lewon.dofus.bot.sniffer.model.types.game.character.spellmodifier

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SpellModifierMessage : NetworkType() {
	var spellId: Int = 0
	var actionType: Int = 0
	var modifierType: Int = 0
	var context: Int = 0
	var equipment: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		spellId = stream.readVarShort().toInt()
		actionType = stream.readUnsignedByte().toInt()
		modifierType = stream.readUnsignedByte().toInt()
		context = stream.readInt().toInt()
		equipment = stream.readInt().toInt()
	}
}
