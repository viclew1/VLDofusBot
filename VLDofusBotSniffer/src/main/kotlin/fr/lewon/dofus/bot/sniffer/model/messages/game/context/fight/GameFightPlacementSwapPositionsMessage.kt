package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.context.IdentifiedEntityDispositionInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightPlacementSwapPositionsMessage : NetworkMessage() {
	var dispositions: ArrayList<IdentifiedEntityDispositionInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		dispositions = ArrayList()
		for (i in 0 until 2) {
			val item = IdentifiedEntityDispositionInformations()
			item.deserialize(stream)
			dispositions.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 7448
}
