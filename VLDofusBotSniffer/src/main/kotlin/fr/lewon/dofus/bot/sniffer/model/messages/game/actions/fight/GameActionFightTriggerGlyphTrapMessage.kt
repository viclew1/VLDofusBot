package fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight

import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.AbstractGameActionMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionFightTriggerGlyphTrapMessage : AbstractGameActionMessage() {
	var markId: Int = 0
	var markImpactCell: Int = 0
	var triggeringCharacterId: Double = 0.0
	var triggeredSpellId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		markId = stream.readUnsignedShort().toInt()
		markImpactCell = stream.readVarShort().toInt()
		triggeringCharacterId = stream.readDouble().toDouble()
		triggeredSpellId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 5767
}
