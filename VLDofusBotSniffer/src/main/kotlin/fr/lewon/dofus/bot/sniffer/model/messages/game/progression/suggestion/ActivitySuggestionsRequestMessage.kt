package fr.lewon.dofus.bot.sniffer.model.messages.game.progression.suggestion

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ActivitySuggestionsRequestMessage : NetworkMessage() {
	var minLevel: Int = 0
	var maxLevel: Int = 0
	var areaId: Int = 0
	var activityCategoryId: Int = 0
	var nbCards: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		minLevel = stream.readVarShort().toInt()
		maxLevel = stream.readVarShort().toInt()
		areaId = stream.readVarShort().toInt()
		activityCategoryId = stream.readVarShort().toInt()
		nbCards = stream.readUnsignedShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 4158
}
