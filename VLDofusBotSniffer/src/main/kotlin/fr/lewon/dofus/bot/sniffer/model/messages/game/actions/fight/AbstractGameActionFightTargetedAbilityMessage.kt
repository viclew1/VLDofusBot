package fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight

import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.AbstractGameActionMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AbstractGameActionFightTargetedAbilityMessage : AbstractGameActionMessage() {
	var silentCast: Boolean = false
	var verboseCast: Boolean = false
	var targetId: Double = 0.0
	var destinationCellId: Int = 0
	var critical: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		silentCast = BooleanByteWrapper.getFlag(_box0, 0)
		verboseCast = BooleanByteWrapper.getFlag(_box0, 1)
		targetId = stream.readDouble().toDouble()
		destinationCellId = stream.readUnsignedShort().toInt()
		critical = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 3673
}
