package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AbstractPartyMemberInFightMessage : AbstractPartyMessage() {
	var reason: Int = 0
	var memberId: Double = 0.0
	var memberAccountId: Int = 0
	var memberName: String = ""
	var fightId: Int = 0
	var timeBeforeFightStart: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		reason = stream.readUnsignedByte().toInt()
		memberId = stream.readVarLong().toDouble()
		memberAccountId = stream.readInt().toInt()
		memberName = stream.readUTF()
		fightId = stream.readVarShort().toInt()
		timeBeforeFightStart = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 6717
}
