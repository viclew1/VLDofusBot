package fr.lewon.dofus.bot.sniffer.model.messages.web.haapi

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HaapiConfirmationMessage : NetworkMessage() {
	var kamas: Double = 0.0
	var amount: Double = 0.0
	var rate: Int = 0
	var action: Int = 0
	var transaction: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		kamas = stream.readVarLong().toDouble()
		amount = stream.readVarLong().toDouble()
		rate = stream.readVarShort().toInt()
		action = stream.readUnsignedByte().toInt()
		transaction = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 1734
}
