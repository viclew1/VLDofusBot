package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.treasureHunt

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TreasureHuntShowLegendaryUIMessage : NetworkMessage() {
	var availableLegendaryIds: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		availableLegendaryIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			availableLegendaryIds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9036
}
