package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HumanOptionOrnament : HumanOption() {
	var ornamentId: Int = 0
	var level: Int = 0
	var leagueId: Int = 0
	var ladderPosition: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		ornamentId = stream.readVarShort().toInt()
		level = stream.readVarShort().toInt()
		leagueId = stream.readVarShort().toInt()
		ladderPosition = stream.readInt().toInt()
	}
}
