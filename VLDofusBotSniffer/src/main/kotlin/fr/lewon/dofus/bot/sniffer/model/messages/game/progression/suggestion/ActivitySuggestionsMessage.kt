package fr.lewon.dofus.bot.sniffer.model.messages.game.progression.suggestion

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ActivitySuggestionsMessage : NetworkMessage() {
	var lockedActivitiesIds: ArrayList<Int> = ArrayList()
	var unlockedActivitiesIds: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		lockedActivitiesIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			lockedActivitiesIds.add(item)
		}
		unlockedActivitiesIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			unlockedActivitiesIds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 6457
}
