package fr.lewon.dofus.bot.sniffer.model.types.game.alliance

import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.sniffer.model.types.game.social.SocialMember
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceMemberInfo : SocialMember() {
	var avaRoleId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		avaRoleId = stream.readInt().toInt()
	}
}
