package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.GoldItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GoldAddedMessage : NetworkMessage() {
	lateinit var gold: GoldItem
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		gold = GoldItem()
		gold.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 6571
}
