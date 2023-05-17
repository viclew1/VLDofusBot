package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightTeamMemberTaxCollectorInformations : FightTeamMemberInformations() {
	var firstNameId: Int = 0
	var lastNameId: Int = 0
	var groupId: Int = 0
	var uid: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		firstNameId = stream.readVarShort().toInt()
		lastNameId = stream.readVarShort().toInt()
		groupId = stream.readVarInt().toInt()
		uid = stream.readDouble().toDouble()
	}
}
