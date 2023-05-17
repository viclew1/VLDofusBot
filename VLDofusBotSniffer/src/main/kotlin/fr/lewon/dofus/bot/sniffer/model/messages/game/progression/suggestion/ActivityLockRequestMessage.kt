package fr.lewon.dofus.bot.sniffer.model.messages.game.progression.suggestion

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ActivityLockRequestMessage : NetworkMessage() {
	var activityId: Int = 0
	var lock: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		activityId = stream.readVarShort().toInt()
		lock = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 7347
}
