package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance.application

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceUpdateApplicationMessage : NetworkMessage() {
	var applyText: String = ""
	var allianceId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		applyText = stream.readUTF()
		allianceId = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 6880
}
