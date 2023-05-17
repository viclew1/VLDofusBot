package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceModificationStartedMessage : NetworkMessage() {
	var canChangeName: Boolean = false
	var canChangeTag: Boolean = false
	var canChangeEmblem: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		canChangeName = BooleanByteWrapper.getFlag(_box0, 0)
		canChangeTag = BooleanByteWrapper.getFlag(_box0, 1)
		canChangeEmblem = BooleanByteWrapper.getFlag(_box0, 2)
	}
	override fun getNetworkMessageId(): Int = 71
}
