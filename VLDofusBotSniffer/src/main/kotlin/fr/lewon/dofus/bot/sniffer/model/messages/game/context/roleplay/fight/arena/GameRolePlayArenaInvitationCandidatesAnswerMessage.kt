package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.fight.arena.LeagueFriendInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayArenaInvitationCandidatesAnswerMessage : NetworkMessage() {
	var candidates: ArrayList<LeagueFriendInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		candidates = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = LeagueFriendInformations()
			item.deserialize(stream)
			candidates.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 3303
}
