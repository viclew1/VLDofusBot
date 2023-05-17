package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightCommonInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayShowChallengeMessage : NetworkMessage() {
	lateinit var commonsInfos: FightCommonInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		commonsInfos = FightCommonInformations()
		commonsInfos.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 6779
}
