package fr.lewon.dofus.bot.sniffer.model.messages.game.prism

import fr.lewon.dofus.bot.sniffer.model.types.game.prism.PrismGeolocalizedInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PrismAddOrUpdateMessage : NetworkMessage() {
	lateinit var prism: PrismGeolocalizedInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		prism = PrismGeolocalizedInformation()
		prism.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 1687
}
