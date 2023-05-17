package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.entity.PartyEntityBaseInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyMemberArenaInformations : PartyMemberInformations() {
	var rank: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		rank = stream.readVarShort().toInt()
	}
}
