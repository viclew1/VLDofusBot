package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MimicryObjectErrorMessage : SymbioticObjectErrorMessage() {
	var preview: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		preview = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 5728
}
