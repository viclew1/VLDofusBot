package fr.lewon.dofus.bot.sniffer.model.messages.connection

import fr.lewon.dofus.bot.sniffer.model.types.connection.GameServerInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ServerStatusUpdateMessage : NetworkMessage() {
	lateinit var server: GameServerInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		server = GameServerInformations()
		server.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 3516
}
