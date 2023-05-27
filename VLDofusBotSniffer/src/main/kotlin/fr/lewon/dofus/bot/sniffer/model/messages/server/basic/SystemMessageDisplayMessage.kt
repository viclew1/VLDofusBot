package fr.lewon.dofus.bot.sniffer.model.messages.server.basic

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SystemMessageDisplayMessage : NetworkMessage() {
	var hangUp: Boolean = false
	var msgId: Int = 0
	var parameters: ArrayList<String> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		hangUp = stream.readBoolean()
		msgId = stream.readVarShort().toInt()
		parameters = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUTF()
			parameters.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 6288
}
