package fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount

import fr.lewon.dofus.bot.sniffer.model.types.game.paddock.PaddockItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameDataPaddockObjectAddMessage : NetworkMessage() {
	lateinit var paddockItemDescription: PaddockItem
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		paddockItemDescription = PaddockItem()
		paddockItemDescription.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 8777
}
