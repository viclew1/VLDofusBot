package fr.lewon.dofus.bot.sniffer.model.messages.game.social

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SocialNoticeMessage : NetworkMessage() {
	var content: String = ""
	var timestamp: Int = 0
	var memberId: Double = 0.0
	var memberName: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		content = stream.readUTF()
		timestamp = stream.readInt().toInt()
		memberId = stream.readVarLong().toDouble()
		memberName = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 1872
}
