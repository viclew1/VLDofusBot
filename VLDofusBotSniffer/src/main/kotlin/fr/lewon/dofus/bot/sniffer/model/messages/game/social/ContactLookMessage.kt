package fr.lewon.dofus.bot.sniffer.model.messages.game.social

import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ContactLookMessage : NetworkMessage() {
	var requestId: Int = 0
	var playerName: String = ""
	var playerId: Double = 0.0
	lateinit var look: EntityLook
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		requestId = stream.readVarInt().toInt()
		playerName = stream.readUTF()
		playerId = stream.readVarLong().toDouble()
		look = EntityLook()
		look.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 9243
}
