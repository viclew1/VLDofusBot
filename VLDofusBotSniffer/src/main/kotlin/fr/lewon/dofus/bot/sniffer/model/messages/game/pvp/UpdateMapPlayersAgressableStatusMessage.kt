package fr.lewon.dofus.bot.sniffer.model.messages.game.pvp

import fr.lewon.dofus.bot.sniffer.model.types.game.pvp.AgressableStatusMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class UpdateMapPlayersAgressableStatusMessage : NetworkMessage() {
	var playerAvAMessages: ArrayList<AgressableStatusMessage> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerAvAMessages = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = AgressableStatusMessage()
			item.deserialize(stream)
			playerAvAMessages.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 5499
}
