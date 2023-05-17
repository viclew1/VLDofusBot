package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.PartyGuestInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.PartyInvitationMemberInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyInvitationDetailsMessage : AbstractPartyMessage() {
	var partyType: Int = 0
	var partyName: String = ""
	var fromId: Double = 0.0
	var fromName: String = ""
	var leaderId: Double = 0.0
	var members: ArrayList<PartyInvitationMemberInformations> = ArrayList()
	var guests: ArrayList<PartyGuestInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		partyType = stream.readUnsignedByte().toInt()
		partyName = stream.readUTF()
		fromId = stream.readVarLong().toDouble()
		fromName = stream.readUTF()
		leaderId = stream.readVarLong().toDouble()
		members = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<PartyInvitationMemberInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			members.add(item)
		}
		guests = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = PartyGuestInformations()
			item.deserialize(stream)
			guests.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 736
}
