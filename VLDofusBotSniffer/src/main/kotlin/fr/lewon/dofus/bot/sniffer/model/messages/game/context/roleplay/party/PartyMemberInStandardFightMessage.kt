package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.context.MapCoordinatesExtended
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyMemberInStandardFightMessage : AbstractPartyMemberInFightMessage() {
	lateinit var fightMap: MapCoordinatesExtended
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fightMap = MapCoordinatesExtended()
		fightMap.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 6110
}
