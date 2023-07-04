package fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight

import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.AbstractGameActionMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionFightTackledMessage : AbstractGameActionMessage() {
	var tacklersIds: ArrayList<Double> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		tacklersIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readDouble().toDouble()
			tacklersIds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 5588
}
