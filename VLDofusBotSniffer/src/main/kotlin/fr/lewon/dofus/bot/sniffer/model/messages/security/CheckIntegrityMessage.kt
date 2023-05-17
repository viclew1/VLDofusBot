package fr.lewon.dofus.bot.sniffer.model.messages.security

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CheckIntegrityMessage : NetworkMessage() {
	var data: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		data = ArrayList()
		for (i in 0 until stream.readVarInt().toInt()) {
			val item = stream.readUnsignedByte().toInt()
			data.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 3785
}
