package fr.lewon.dofus.bot.sniffer.model.messages.game.chat.smiley

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MoodSmileyResultMessage : NetworkMessage() {
	var resultCode: Int = 0
	var smileyId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		resultCode = stream.readUnsignedByte().toInt()
		smileyId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 6617
}
