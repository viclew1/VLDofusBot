package fr.lewon.dofus.bot.sniffer.model.messages.game.context

import fr.lewon.dofus.bot.sniffer.model.types.game.context.EntityMovementInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameContextMoveMultipleElementsMessage : NetworkMessage() {
	var movements: ArrayList<EntityMovementInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		movements = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = EntityMovementInformations()
			item.deserialize(stream)
			movements.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4844
}
