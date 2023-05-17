package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DecraftedItemStackInfo : NetworkType() {
	var objectUID: Int = 0
	var bonusMin: Double = 0.0
	var bonusMax: Double = 0.0
	var runesId: ArrayList<Int> = ArrayList()
	var runesQty: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objectUID = stream.readVarInt().toInt()
		bonusMin = stream.readFloat().toDouble()
		bonusMax = stream.readFloat().toDouble()
		runesId = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			runesId.add(item)
		}
		runesQty = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			runesQty.add(item)
		}
	}
}
