package fr.lewon.dofus.bot.sniffer.model.types.game.character.choice

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterToRemodelInformations : CharacterRemodelingInformation() {
	var possibleChangeMask: Int = 0
	var mandatoryChangeMask: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		possibleChangeMask = stream.readUnsignedByte().toInt()
		mandatoryChangeMask = stream.readUnsignedByte().toInt()
	}
}
