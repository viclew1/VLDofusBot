package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightStartingMessage : NetworkMessage() {
	var fightType: Int = 0
	var fightId: Int = 0
	var attackerId: Double = 0.0
	var defenderId: Double = 0.0
	var containsBoss: Boolean = false
	var monsters: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fightType = stream.readUnsignedByte().toInt()
		fightId = stream.readVarShort().toInt()
		attackerId = stream.readDouble().toDouble()
		defenderId = stream.readDouble().toDouble()
		containsBoss = stream.readBoolean()
		monsters = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			monsters.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1962
}
