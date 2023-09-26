package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MimicryObjectPreviewMessage : NetworkMessage() {
	lateinit var result: ObjectItem
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		result = ObjectItem()
		result.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 5947
}
