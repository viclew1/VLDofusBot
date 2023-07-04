package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.lockable

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class LockableUseCodeMessage : NetworkMessage() {
	var code: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		code = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 2014
}
