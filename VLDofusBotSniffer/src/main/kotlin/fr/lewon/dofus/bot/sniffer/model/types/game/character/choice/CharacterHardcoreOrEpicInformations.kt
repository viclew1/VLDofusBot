package fr.lewon.dofus.bot.sniffer.model.types.game.character.choice

import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterHardcoreOrEpicInformations : CharacterBaseInformations() {
	var deathState: Int = 0
	var deathCount: Int = 0
	var deathMaxLevel: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		deathState = stream.readUnsignedByte().toInt()
		deathCount = stream.readVarShort().toInt()
		deathMaxLevel = stream.readVarShort().toInt()
	}
}
