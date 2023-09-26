package fr.lewon.dofus.bot.sniffer.model.messages.game.chat

import fr.lewon.dofus.bot.sniffer.model.types.common.AbstractPlayerSearchInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ChatClientPrivateMessage : ChatAbstractClientMessage() {
	lateinit var receiver: AbstractPlayerSearchInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		receiver = ProtocolTypeManager.getInstance<AbstractPlayerSearchInformation>(stream.readUnsignedShort())
		receiver.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 7530
}
