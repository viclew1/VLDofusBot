package fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ObjectEffectDice : ObjectEffect() {
	var diceNum: Int = 0
	var diceSide: Int = 0
	var diceConst: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		diceNum = stream.readVarInt().toInt()
		diceSide = stream.readVarInt().toInt()
		diceConst = stream.readVarInt().toInt()
	}
}
