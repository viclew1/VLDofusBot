package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SymbioticObjectAssociateRequestMessage : NetworkMessage() {
	var symbioteUID: Int = 0
	var symbiotePos: Int = 0
	var hostUID: Int = 0
	var hostPos: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		symbioteUID = stream.readVarInt().toInt()
		symbiotePos = stream.readUnsignedByte().toInt()
		hostUID = stream.readVarInt().toInt()
		hostPos = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 9129
}
