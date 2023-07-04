package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.job

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.job.JobBookSubscription
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class JobBookSubscriptionMessage : NetworkMessage() {
	var subscriptions: ArrayList<JobBookSubscription> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		subscriptions = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = JobBookSubscription()
			item.deserialize(stream)
			subscriptions.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 5452
}
