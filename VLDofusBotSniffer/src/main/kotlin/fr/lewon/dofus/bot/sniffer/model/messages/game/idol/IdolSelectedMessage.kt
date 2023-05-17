package fr.lewon.dofus.bot.sniffer.model.messages.game.idol

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IdolSelectedMessage : NetworkMessage() {
	var activate: Boolean = false
	var party: Boolean = false
	var idolId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		activate = BooleanByteWrapper.getFlag(_box0, 0)
		party = BooleanByteWrapper.getFlag(_box0, 1)
		idolId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 4793
}
