package fr.lewon.dofus.bot.sniffer.model.messages.game.moderation

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PopupWarningMessage : NetworkMessage() {
	var lockDuration: Int = 0
	var author: String = ""
	var content: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		lockDuration = stream.readUnsignedByte().toInt()
		author = stream.readUTF()
		content = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 5970
}
