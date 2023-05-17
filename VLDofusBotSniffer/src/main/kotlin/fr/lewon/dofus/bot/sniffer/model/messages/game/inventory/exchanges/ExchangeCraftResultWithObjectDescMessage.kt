package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItemNotInContainer
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeCraftResultWithObjectDescMessage : ExchangeCraftResultMessage() {
	lateinit var objectInfo: ObjectItemNotInContainer
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objectInfo = ObjectItemNotInContainer()
		objectInfo.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 6151
}
