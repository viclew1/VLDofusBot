package fr.lewon.dofus.bot.sniffer.model.messages.connection

import fr.lewon.dofus.bot.sniffer.model.types.connection.GameServerInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ServersListMessage : NetworkMessage() {
	var servers: ArrayList<GameServerInformations> = ArrayList()
	var canCreateNewCharacter: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		servers = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GameServerInformations()
			item.deserialize(stream)
			servers.add(item)
		}
		canCreateNewCharacter = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 3434
}
