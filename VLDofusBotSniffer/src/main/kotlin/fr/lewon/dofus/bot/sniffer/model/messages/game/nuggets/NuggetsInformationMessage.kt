package fr.lewon.dofus.bot.sniffer.model.messages.game.nuggets

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class NuggetsInformationMessage : NetworkMessage() {
	var nuggetsQuantity: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		nuggetsQuantity = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 1617
}
