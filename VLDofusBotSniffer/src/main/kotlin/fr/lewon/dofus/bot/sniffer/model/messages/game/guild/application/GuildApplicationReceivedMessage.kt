package fr.lewon.dofus.bot.sniffer.model.messages.game.guild.application

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildApplicationReceivedMessage : NetworkMessage() {
	var playerName: String = ""
	var playerId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerName = stream.readUTF()
		playerId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 8112
}
