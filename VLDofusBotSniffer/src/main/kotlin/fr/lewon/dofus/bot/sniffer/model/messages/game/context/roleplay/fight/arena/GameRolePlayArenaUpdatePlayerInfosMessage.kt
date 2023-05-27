package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.fight.arena.ArenaRankInfos
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayArenaUpdatePlayerInfosMessage : NetworkMessage() {
	lateinit var solo: ArenaRankInfos
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		solo = ArenaRankInfos()
		solo.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 7026
}
