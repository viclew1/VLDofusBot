package fr.lewon.dofus.bot.sniffer.model.messages.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.game.friend.AcquaintanceInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AcquaintanceAddedMessage : NetworkMessage() {
	lateinit var acquaintanceAdded: AcquaintanceInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		acquaintanceAdded = ProtocolTypeManager.getInstance<AcquaintanceInformation>(stream.readUnsignedShort())
		acquaintanceAdded.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 4660
}
