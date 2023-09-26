package fr.lewon.dofus.bot.sniffer.model.messages.game.context

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.MonsterBoosts
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRefreshMonsterBoostsMessage : NetworkMessage() {
	var monsterBoosts: ArrayList<MonsterBoosts> = ArrayList()
	var familyBoosts: ArrayList<MonsterBoosts> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		monsterBoosts = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = MonsterBoosts()
			item.deserialize(stream)
			monsterBoosts.add(item)
		}
		familyBoosts = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = MonsterBoosts()
			item.deserialize(stream)
			familyBoosts.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9112
}
