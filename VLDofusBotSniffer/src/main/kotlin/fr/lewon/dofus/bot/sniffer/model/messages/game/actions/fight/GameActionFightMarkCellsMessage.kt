package fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight

import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.AbstractGameActionMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.actions.fight.GameActionMark
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionFightMarkCellsMessage : AbstractGameActionMessage() {
	lateinit var mark: GameActionMark
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		mark = GameActionMark()
		mark.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 8242
}
