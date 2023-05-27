package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeMountsTakenFromPaddockMessage : NetworkMessage() {
	var name: String = ""
	var worldX: Int = 0
	var worldY: Int = 0
	var ownername: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		name = stream.readUTF()
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		ownername = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 9477
}
