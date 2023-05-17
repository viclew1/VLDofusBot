package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MimicryObjectFeedAndAssociateRequestMessage : SymbioticObjectAssociateRequestMessage() {
	var foodUID: Int = 0
	var foodPos: Int = 0
	var preview: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		foodUID = stream.readVarInt().toInt()
		foodPos = stream.readUnsignedByte().toInt()
		preview = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 8003
}
