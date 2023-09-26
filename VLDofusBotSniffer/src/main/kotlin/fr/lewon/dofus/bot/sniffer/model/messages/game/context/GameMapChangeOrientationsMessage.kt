package fr.lewon.dofus.bot.sniffer.model.messages.game.context

import fr.lewon.dofus.bot.sniffer.model.types.game.context.ActorOrientation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameMapChangeOrientationsMessage : NetworkMessage() {
	var orientations: ArrayList<ActorOrientation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		orientations = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ActorOrientation()
			item.deserialize(stream)
			orientations.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 443
}
