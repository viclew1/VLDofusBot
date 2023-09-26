package fr.lewon.dofus.bot.sniffer.model.messages.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.game.friend.FriendInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FriendAddedMessage : NetworkMessage() {
	lateinit var friendAdded: FriendInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		friendAdded = ProtocolTypeManager.getInstance<FriendInformations>(stream.readUnsignedShort())
		friendAdded.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 2637
}
