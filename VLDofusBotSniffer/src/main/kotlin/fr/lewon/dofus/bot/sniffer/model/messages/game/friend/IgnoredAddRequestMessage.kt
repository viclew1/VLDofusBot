package fr.lewon.dofus.bot.sniffer.model.messages.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.common.AbstractPlayerSearchInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IgnoredAddRequestMessage : NetworkMessage() {
	lateinit var target: AbstractPlayerSearchInformation
	var session: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		target = ProtocolTypeManager.getInstance<AbstractPlayerSearchInformation>(stream.readUnsignedShort())
		target.deserialize(stream)
		session = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 7867
}
