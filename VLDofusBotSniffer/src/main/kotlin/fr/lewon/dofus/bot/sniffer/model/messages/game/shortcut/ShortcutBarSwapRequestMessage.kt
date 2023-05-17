package fr.lewon.dofus.bot.sniffer.model.messages.game.shortcut

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ShortcutBarSwapRequestMessage : NetworkMessage() {
	var barType: Int = 0
	var firstSlot: Int = 0
	var secondSlot: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		barType = stream.readUnsignedByte().toInt()
		firstSlot = stream.readUnsignedByte().toInt()
		secondSlot = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 3829
}
