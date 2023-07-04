package fr.lewon.dofus.bot.sniffer.model.messages.web.haapi

import fr.lewon.dofus.bot.sniffer.model.types.web.haapi.BufferInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HaapiBufferListMessage : NetworkMessage() {
	var buffers: ArrayList<BufferInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		buffers = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = BufferInformation()
			item.deserialize(stream)
			buffers.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 7406
}
