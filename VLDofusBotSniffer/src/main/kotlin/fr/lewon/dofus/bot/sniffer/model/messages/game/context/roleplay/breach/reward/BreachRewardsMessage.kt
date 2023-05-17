package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.breach.reward

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.breach.BreachReward
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BreachRewardsMessage : NetworkMessage() {
	var rewards: ArrayList<BreachReward> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		rewards = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = BreachReward()
			item.deserialize(stream)
			rewards.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 971
}
