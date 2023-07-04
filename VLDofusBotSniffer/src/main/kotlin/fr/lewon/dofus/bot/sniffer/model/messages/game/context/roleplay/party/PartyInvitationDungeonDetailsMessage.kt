package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.PartyGuestInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.PartyInvitationMemberInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyInvitationDungeonDetailsMessage : PartyInvitationDetailsMessage() {
	var dungeonId: Int = 0
	var playersDungeonReady: ArrayList<Boolean> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		dungeonId = stream.readVarShort().toInt()
		playersDungeonReady = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readBoolean()
			playersDungeonReady.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 8279
}
