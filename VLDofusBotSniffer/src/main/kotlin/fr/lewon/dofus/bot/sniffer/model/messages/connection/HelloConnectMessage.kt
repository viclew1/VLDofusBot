package fr.lewon.dofus.bot.sniffer.model.messages.connection

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HelloConnectMessage : NetworkMessage() {
	var salt: String = ""
	var key: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		salt = stream.readUTF()
		key = ArrayList()
		for (i in 0 until stream.readVarInt().toInt()) {
			val item = stream.readUnsignedByte().toInt()
			key.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 2909
}
