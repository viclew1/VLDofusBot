package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyFollowStatusUpdateMessage : AbstractPartyMessage() {
	var success: Boolean = false
	var isFollowed: Boolean = false
	var followedId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		success = BooleanByteWrapper.getFlag(_box0, 0)
		isFollowed = BooleanByteWrapper.getFlag(_box0, 1)
		followedId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 2782
}
