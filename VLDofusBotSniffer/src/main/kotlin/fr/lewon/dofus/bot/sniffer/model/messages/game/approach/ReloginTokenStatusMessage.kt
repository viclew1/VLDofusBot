package fr.lewon.dofus.bot.sniffer.model.messages.game.approach

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ReloginTokenStatusMessage : NetworkMessage() {
	var validToken: Boolean = false
	var token: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		validToken = stream.readBoolean()
		token = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 3729
}
