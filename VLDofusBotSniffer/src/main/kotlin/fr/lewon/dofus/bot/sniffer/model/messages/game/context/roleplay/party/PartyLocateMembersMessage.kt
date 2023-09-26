package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.PartyMemberGeoPosition
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyLocateMembersMessage : AbstractPartyMessage() {
	var geopositions: ArrayList<PartyMemberGeoPosition> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		geopositions = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = PartyMemberGeoPosition()
			item.deserialize(stream)
			geopositions.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 3224
}
