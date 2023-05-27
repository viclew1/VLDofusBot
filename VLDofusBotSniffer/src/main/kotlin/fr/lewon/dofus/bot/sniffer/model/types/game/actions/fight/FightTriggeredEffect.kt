package fr.lewon.dofus.bot.sniffer.model.types.game.actions.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightTriggeredEffect : AbstractFightDispellableEffect() {
	var param1: Int = 0
	var param2: Int = 0
	var param3: Int = 0
	var delay: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		param1 = stream.readInt().toInt()
		param2 = stream.readInt().toInt()
		param3 = stream.readInt().toInt()
		delay = stream.readUnsignedShort().toInt()
	}
}
