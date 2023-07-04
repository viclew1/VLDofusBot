package fr.lewon.dofus.bot.sniffer.model.messages.game.startup

import fr.lewon.dofus.bot.sniffer.model.types.game.startup.GameActionItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionItemAddMessage : NetworkMessage() {
	lateinit var newAction: GameActionItem
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		newAction = GameActionItem()
		newAction.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 8600
}
