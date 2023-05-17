package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightTeamMemberMonsterInformations : FightTeamMemberInformations() {
	var monsterId: Int = 0
	var grade: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		monsterId = stream.readInt().toInt()
		grade = stream.readUnsignedByte().toInt()
	}
}
