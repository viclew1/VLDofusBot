package fr.lewon.dofus.bot.sniffer.model.messages.game.guild.application

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildApplicationListenMessage : NetworkMessage() {
	var listen: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		listen = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 1821
}
