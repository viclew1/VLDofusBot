package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.npc

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.npc.MapNpcQuestInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ListMapNpcsQuestStatusUpdateMessage : NetworkMessage() {
	var mapInfo: ArrayList<MapNpcQuestInfo> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		mapInfo = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = MapNpcQuestInfo()
			item.deserialize(stream)
			mapInfo.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 3678
}
