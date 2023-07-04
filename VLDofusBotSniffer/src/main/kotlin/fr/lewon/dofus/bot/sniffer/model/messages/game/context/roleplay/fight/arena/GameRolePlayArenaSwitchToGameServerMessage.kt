package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayArenaSwitchToGameServerMessage : NetworkMessage() {
	var validToken: Boolean = false
	var token: String = ""
	var homeServerId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		validToken = stream.readBoolean()
		token = stream.readUTF()
		homeServerId = stream.readUnsignedShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 9037
}
