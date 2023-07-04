package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightTurnListMessage : NetworkMessage() {
	var ids: ArrayList<Double> = ArrayList()
	var deadsIds: ArrayList<Double> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		ids = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readDouble().toDouble()
			ids.add(item)
		}
		deadsIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readDouble().toDouble()
			deadsIds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 102
}
