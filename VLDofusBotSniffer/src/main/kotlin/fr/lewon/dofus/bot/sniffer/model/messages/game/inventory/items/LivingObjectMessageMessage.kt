package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class LivingObjectMessageMessage : NetworkMessage() {
	var msgId: Int = 0
	var timeStamp: Int = 0
	var owner: String = ""
	var objectGenericId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		msgId = stream.readVarShort().toInt()
		timeStamp = stream.readInt().toInt()
		owner = stream.readUTF()
		objectGenericId = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 1590
}
