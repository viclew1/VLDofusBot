package fr.lewon.dofus.bot.sniffer.model.messages.authorized

import fr.lewon.dofus.bot.sniffer.model.types.game.Uuid
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AdminCommandMessage : NetworkMessage() {
	lateinit var messageUuid: Uuid
	var content: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		messageUuid = Uuid()
		messageUuid.deserialize(stream)
		content = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 3305
}
