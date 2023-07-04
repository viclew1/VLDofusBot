package fr.lewon.dofus.bot.sniffer.model.messages.connection

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ForceAccountStatusMessage : NetworkMessage() {
	var force: Boolean = false
	var forcedAccountId: Int = 0
	var forcedNickname: String = ""
	var forcedTag: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		force = stream.readBoolean()
		forcedAccountId = stream.readInt().toInt()
		forcedNickname = stream.readUTF()
		forcedTag = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 7528
}
