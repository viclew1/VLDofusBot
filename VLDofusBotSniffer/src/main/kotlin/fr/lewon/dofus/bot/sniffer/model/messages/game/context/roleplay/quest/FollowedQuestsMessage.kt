package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.quest

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.quest.QuestActiveDetailedInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FollowedQuestsMessage : NetworkMessage() {
	var quests: ArrayList<QuestActiveDetailedInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		quests = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = QuestActiveDetailedInformations()
			item.deserialize(stream)
			quests.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4336
}
