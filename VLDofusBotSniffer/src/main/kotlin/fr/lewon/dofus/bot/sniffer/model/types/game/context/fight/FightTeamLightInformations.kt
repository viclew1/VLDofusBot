package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightTeamLightInformations : AbstractFightTeamInformations() {
	var hasFriend: Boolean = false
	var hasGuildMember: Boolean = false
	var hasAllianceMember: Boolean = false
	var hasGroupMember: Boolean = false
	var hasMyTaxCollector: Boolean = false
	var teamMembersCount: Int = 0
	var meanLevel: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		hasFriend = BooleanByteWrapper.getFlag(_box0, 0)
		hasGuildMember = BooleanByteWrapper.getFlag(_box0, 1)
		hasAllianceMember = BooleanByteWrapper.getFlag(_box0, 2)
		hasGroupMember = BooleanByteWrapper.getFlag(_box0, 3)
		hasMyTaxCollector = BooleanByteWrapper.getFlag(_box0, 4)
		teamMembersCount = stream.readUnsignedByte().toInt()
		meanLevel = stream.readVarInt().toInt()
	}
}
