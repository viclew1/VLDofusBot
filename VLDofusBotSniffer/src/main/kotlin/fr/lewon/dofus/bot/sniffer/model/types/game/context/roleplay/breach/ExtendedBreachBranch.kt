package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.breach

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.MonsterInGroupLightInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExtendedBreachBranch : BreachBranch() {
	var rewards: ArrayList<BreachReward> = ArrayList()
	var modifier: Int = 0
	var prize: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		rewards = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = BreachReward()
			item.deserialize(stream)
			rewards.add(item)
		}
		modifier = stream.readVarInt().toInt()
		prize = stream.readVarInt().toInt()
	}
}
