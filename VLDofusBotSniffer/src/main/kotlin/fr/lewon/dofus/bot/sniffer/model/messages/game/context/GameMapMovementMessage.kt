package fr.lewon.dofus.bot.sniffer.model.messages.game.context

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameMapMovementMessage : NetworkMessage() {
	var keyMovements: ArrayList<Int> = ArrayList()
	var forcedDirection: Int = 0
	var actorId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		keyMovements = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUnsignedShort().toInt()
			keyMovements.add(item)
		}
		forcedDirection = stream.readUnsignedShort().toInt()
		actorId = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 6174
}
