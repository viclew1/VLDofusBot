package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobBookSubscription : NetworkType() {
	var jobId: Int = 0
	var subscribed: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		jobId = stream.readUnsignedByte().toInt()
		subscribed = stream.readBoolean()
	}
}
