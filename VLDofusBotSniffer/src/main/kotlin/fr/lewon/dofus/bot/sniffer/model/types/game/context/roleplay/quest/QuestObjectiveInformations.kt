package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.quest

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class QuestObjectiveInformations : NetworkType() {
	var objectiveId: Int = 0
	var objectiveStatus: Boolean = false
	var dialogParams: ArrayList<String> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objectiveId = stream.readVarShort().toInt()
		objectiveStatus = stream.readBoolean()
		dialogParams = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUTF()
			dialogParams.add(item)
		}
	}
}
