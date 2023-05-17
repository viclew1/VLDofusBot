package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class LivingObjectMessageRequestMessage : NetworkMessage() {
	var msgId: Int = 0
	var parameters: ArrayList<String> = ArrayList()
	var livingObject: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		msgId = stream.readVarShort().toInt()
		parameters = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUTF()
			parameters.add(item)
		}
		livingObject = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 684
}
