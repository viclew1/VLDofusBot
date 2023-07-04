package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.visual

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlaySpellAnimMessage : NetworkMessage() {
	var casterId: Double = 0.0
	var targetCellId: Int = 0
	var spellId: Int = 0
	var spellLevel: Int = 0
	var direction: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		casterId = stream.readVarLong().toDouble()
		targetCellId = stream.readVarShort().toInt()
		spellId = stream.readVarShort().toInt()
		spellLevel = stream.readUnsignedShort().toInt()
		direction = stream.readUnsignedShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 8624
}
