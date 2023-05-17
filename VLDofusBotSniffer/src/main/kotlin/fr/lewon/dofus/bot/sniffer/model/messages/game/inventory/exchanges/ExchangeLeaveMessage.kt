package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.messages.game.dialog.LeaveDialogMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeLeaveMessage : LeaveDialogMessage() {
	var success: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		success = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 4024
}
