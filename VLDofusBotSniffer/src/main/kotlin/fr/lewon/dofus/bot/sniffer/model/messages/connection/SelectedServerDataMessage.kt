package fr.lewon.dofus.bot.sniffer.model.messages.connection

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SelectedServerDataMessage : NetworkMessage() {
	var serverId: Int = 0
	var address: String = ""
	var ports: ArrayList<Int> = ArrayList()
	var canCreateNewCharacter: Boolean = false
	var ticket: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		serverId = stream.readVarShort().toInt()
		address = stream.readUTF()
		ports = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			ports.add(item)
		}
		canCreateNewCharacter = stream.readBoolean()
		ticket = ArrayList()
		for (i in 0 until stream.readVarInt().toInt()) {
			val item = stream.readUnsignedByte().toInt()
			ticket.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 5981
}
