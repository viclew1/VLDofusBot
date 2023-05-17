package fr.lewon.dofus.bot.sniffer.model.types.game.data.items

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SellerBuyerDescriptor : NetworkType() {
	var quantities: ArrayList<Int> = ArrayList()
	var types: ArrayList<Int> = ArrayList()
	var taxPercentage: Double = 0.0
	var taxModificationPercentage: Double = 0.0
	var maxItemLevel: Int = 0
	var maxItemPerAccount: Int = 0
	var npcContextualId: Int = 0
	var unsoldDelay: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		quantities = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			quantities.add(item)
		}
		types = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			types.add(item)
		}
		taxPercentage = stream.readFloat().toDouble()
		taxModificationPercentage = stream.readFloat().toDouble()
		maxItemLevel = stream.readUnsignedByte().toInt()
		maxItemPerAccount = stream.readVarInt().toInt()
		npcContextualId = stream.readInt().toInt()
		unsoldDelay = stream.readVarShort().toInt()
	}
}
