package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.quest

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.quest.QuestActiveInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class QuestListMessage : NetworkMessage() {
	var finishedQuestsIds: ArrayList<Int> = ArrayList()
	var finishedQuestsCounts: ArrayList<Int> = ArrayList()
	var activeQuests: ArrayList<QuestActiveInformations> = ArrayList()
	var reinitDoneQuestsIds: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		finishedQuestsIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			finishedQuestsIds.add(item)
		}
		finishedQuestsCounts = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			finishedQuestsCounts.add(item)
		}
		activeQuests = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<QuestActiveInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			activeQuests.add(item)
		}
		reinitDoneQuestsIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			reinitDoneQuestsIds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1025
}
