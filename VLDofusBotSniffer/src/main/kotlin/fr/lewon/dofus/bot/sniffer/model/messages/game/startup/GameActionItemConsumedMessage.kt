package fr.lewon.dofus.bot.sniffer.model.messages.game.startup

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionItemConsumedMessage : NetworkMessage() {
	var success: Boolean = false
	var automaticAction: Boolean = false
	var actionId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		success = BooleanByteWrapper.getFlag(_box0, 0)
		automaticAction = BooleanByteWrapper.getFlag(_box0, 1)
		actionId = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 4643
}
