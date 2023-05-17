package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.PartyGuestInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.PartyMemberInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyJoinMessage : AbstractPartyMessage() {
	var partyType: Int = 0
	var partyLeaderId: Double = 0.0
	var maxParticipants: Int = 0
	var members: ArrayList<PartyMemberInformations> = ArrayList()
	var guests: ArrayList<PartyGuestInformations> = ArrayList()
	var restricted: Boolean = false
	var partyName: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		partyType = stream.readUnsignedByte().toInt()
		partyLeaderId = stream.readVarLong().toDouble()
		maxParticipants = stream.readUnsignedByte().toInt()
		members = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<PartyMemberInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			members.add(item)
		}
		guests = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = PartyGuestInformations()
			item.deserialize(stream)
			guests.add(item)
		}
		restricted = stream.readBoolean()
		partyName = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 2106
}
