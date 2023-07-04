package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.DungeonPartyFinderPlayer
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DungeonPartyFinderRoomContentMessage : NetworkMessage() {
	var dungeonId: Int = 0
	var players: ArrayList<DungeonPartyFinderPlayer> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		dungeonId = stream.readVarShort().toInt()
		players = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = DungeonPartyFinderPlayer()
			item.deserialize(stream)
			players.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 6025
}
