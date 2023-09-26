package fr.lewon.dofus.bot.sniffer.model.messages.game.context.notification

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class NotificationByServerMessage : NetworkMessage() {
	var id: Int = 0
	var parameters: ArrayList<String> = ArrayList()
	var forceOpen: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readVarShort().toInt()
		parameters = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUTF()
			parameters.add(item)
		}
		forceOpen = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 5888
}
