package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.breach

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BreachReward : NetworkType() {
	var id: Int = 0
	var buyLocks: ArrayList<Int> = ArrayList()
	var buyCriterion: String = ""
	var remainingQty: Int = 0
	var price: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readVarInt().toInt()
		buyLocks = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUnsignedByte().toInt()
			buyLocks.add(item)
		}
		buyCriterion = stream.readUTF()
		remainingQty = stream.readVarInt().toInt()
		price = stream.readVarInt().toInt()
	}
}
