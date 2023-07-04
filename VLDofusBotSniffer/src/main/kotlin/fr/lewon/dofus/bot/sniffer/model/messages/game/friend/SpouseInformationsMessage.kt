package fr.lewon.dofus.bot.sniffer.model.messages.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.game.friend.FriendSpouseInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SpouseInformationsMessage : NetworkMessage() {
	lateinit var spouse: FriendSpouseInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		spouse = ProtocolTypeManager.getInstance<FriendSpouseInformations>(stream.readUnsignedShort())
		spouse.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 9828
}
