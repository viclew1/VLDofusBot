package fr.lewon.dofus.bot.sniffer.model.types.game.character.choice

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterMinimalPlusLookInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterBaseInformations : CharacterMinimalPlusLookInformations() {
	var sex: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		sex = stream.readBoolean()
	}
}
