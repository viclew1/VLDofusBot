package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.EntityDispositionInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.quest.GameRolePlayNpcQuestFlag
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayNpcWithQuestInformations : GameRolePlayNpcInformations() {
	lateinit var questFlag: GameRolePlayNpcQuestFlag
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		questFlag = GameRolePlayNpcQuestFlag()
		questFlag.deserialize(stream)
	}
}
