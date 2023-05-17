package fr.lewon.dofus.bot.sniffer.model.messages.web.haapi

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HaapiConfirmationRequestMessage : NetworkMessage() {
	var kamas: Double = 0.0
	var ogrines: Double = 0.0
	var rate: Int = 0
	var action: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		kamas = stream.readVarLong().toDouble()
		ogrines = stream.readVarLong().toDouble()
		rate = stream.readVarShort().toInt()
		action = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 3987
}
