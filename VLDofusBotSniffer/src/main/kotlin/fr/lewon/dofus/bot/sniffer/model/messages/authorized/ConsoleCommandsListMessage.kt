package fr.lewon.dofus.bot.sniffer.model.messages.authorized

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ConsoleCommandsListMessage : NetworkMessage() {
	var aliases: ArrayList<String> = ArrayList()
	var args: ArrayList<String> = ArrayList()
	var descriptions: ArrayList<String> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		aliases = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUTF()
			aliases.add(item)
		}
		args = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUTF()
			args.add(item)
		}
		descriptions = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUTF()
			descriptions.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 5709
}
