package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.DungeonPartyFinderPlayer
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DungeonPartyFinderRoomContentUpdateMessage : NetworkMessage() {
	var dungeonId: Int = 0
	var addedPlayers: ArrayList<DungeonPartyFinderPlayer> = ArrayList()
	var removedPlayersIds: ArrayList<Double> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		dungeonId = stream.readVarShort().toInt()
		addedPlayers = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = DungeonPartyFinderPlayer()
			item.deserialize(stream)
			addedPlayers.add(item)
		}
		removedPlayersIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarLong().toDouble()
			removedPlayersIds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 3247
}
