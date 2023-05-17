package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.npc

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.quest.GameRolePlayNpcQuestFlag
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MapNpcQuestInfo : NetworkType() {
	var mapId: Double = 0.0
	var npcsIdsWithQuest: ArrayList<Int> = ArrayList()
	var questFlags: ArrayList<GameRolePlayNpcQuestFlag> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		mapId = stream.readDouble().toDouble()
		npcsIdsWithQuest = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			npcsIdsWithQuest.add(item)
		}
		questFlags = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GameRolePlayNpcQuestFlag()
			item.deserialize(stream)
			questFlags.add(item)
		}
	}
}
